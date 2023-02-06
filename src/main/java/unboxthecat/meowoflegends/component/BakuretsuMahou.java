package unboxthecat.meowoflegends.component;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.helper.Geometric;

import java.util.*;

public class BakuretsuMahou implements AbilityComponent, Listener {
    //Tunable values
    public static final double COOLDOWN_IN_SECONDS = 3.0;
    public static final double MANA_PERCENT_COST = 0.05;
    public static final int EXPLOSION_TARGET_REACH = 100;
    public static final int EXPLOSION_MAGMA_RADIUS = 20;
    public static final int EXPLOSION_MAGMA_DEPTH = 1;
    public static final int EXPLOSION_MAGMA_HEIGHT = 1;
    public static final double EXPLOSION_MAGMA_CHANNELING_IN_SECONDS = 3.0;
    public static final int EXPLOSION_LAVA_RADIUS = 10;
    public static final int EXPLOSION_LAVA_HEIGHT = 100;

    public static final double EXPLOSION_LAVA_CHANNELING_IN_SECONDS = 1.0;
    public static final double EXPLOSION_LAVA_DURATION_IN_SECONDS = 10.0;
    public static final int EXPLOSION_DAMAGE_RADIUS = 10;
    public static final int EXPLOSION_DAMAGE_HEIGHT = 10;
    public static final double EXPLOSION_DAMAGE = 20.0;


    private MOLEntity owner;
    private final CooldownComponent cooldownComponent;
    private double manaPercentCost;

    public BakuretsuMahou(double cooldownInSeconds, double manaPercentCost) {
        this.cooldownComponent = new CooldownComponent(cooldownInSeconds);
        this.manaPercentCost = manaPercentCost;
    }

    public BakuretsuMahou(Map<String, Object> data) {
        manaPercentCost = (double) data.get("manaPercentCost");
        cooldownComponent = (CooldownComponent)(data.get("cooldownComponent"));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("manaPercentCost", manaPercentCost);
        data.put("cooldownComponent", cooldownComponent);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner) {
        this.owner = owner;
        cooldownComponent.onAttach(owner);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner) {
        HandlerList.unregisterAll(this);
        cooldownComponent.onRemove(owner);
    }

    @EventHandler
    public void trigger(PlayerInteractEvent event) {

        if (isOwner(event.getPlayer()) &&
            isUsingBlazeRod(event.getAction()) &&
            isLookingAtSolidBlock() &&
            isManaSufficient() &&
            isCooldownReady()) {

            applyCost();
            explode();
        }
    }

    private boolean isOwner(Player player) {
        return player.getUniqueId().equals(owner.getEntity().getUniqueId());
    }

    private boolean isUsingBlazeRod(Action action) {
        Player player = (Player)(owner.getEntity());
        return player.getInventory().getHeldItemSlot() == 0 &&
               player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD &&
               (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean isLookingAtSolidBlock() {
        Set<Material> ignoredBlockType = Set.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER, Material.LAVA);
        return ((Player)owner.getEntity()).getTargetBlock(ignoredBlockType, EXPLOSION_TARGET_REACH).getType().isSolid();
    }

    private boolean isManaSufficient() {
        ManaComponent manaComponent = (ManaComponent) owner.getComponent(ManaComponent.class);
        return manaComponent != null && manaComponent.getCurrentMana() >= manaComponent.getMaxMana() * manaPercentCost;
    }

    private boolean isCooldownReady() {
        return cooldownComponent.isReady();
    }

    private void explode() {
        Set<Material> ignoredBlockType = Set.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER, Material.LAVA);
        Block explosionBlock = ((Player)owner.getEntity()).getTargetBlock(ignoredBlockType, EXPLOSION_TARGET_REACH);

        World world = explosionBlock.getWorld();
        Location explosionOrigin = explosionBlock.getLocation();

        //prefetch the blocks first, so the animation looks less laggy
        ArrayList<Block> magmaBlocks = Geometric.getBlockInCylinder(explosionOrigin, EXPLOSION_MAGMA_RADIUS, EXPLOSION_MAGMA_DEPTH, EXPLOSION_MAGMA_HEIGHT,
                b -> b.getType().isSolid() && b.getType().getHardness() >= 0.0);
        Collections.shuffle(magmaBlocks);

        ArrayList<Block> lavaBlocks = Geometric.getBlockInCylinder(explosionOrigin, EXPLOSION_LAVA_RADIUS, 1, EXPLOSION_LAVA_HEIGHT, b -> b.getType().getHardness() >= 0.0);
        lavaBlocks.sort((b1, b2) -> {
            double roundAwayFromZero = b1.getLocation().distanceSquared(explosionOrigin) - b2.getLocation().distanceSquared(explosionOrigin);
            return (int) ((roundAwayFromZero > 0.0) ? Math.ceil(roundAwayFromZero) : Math.floor(roundAwayFromZero));
        });


        //playing channeling sound
        world.playSound(owner.getEntity().getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1.0f, 0.5f);
        world.playSound(explosionOrigin, Sound.BLOCK_LAVA_AMBIENT, 1.0f, 0.5f);

        //create magma block base
        double magmaChannelingInSeconds = EXPLOSION_MAGMA_CHANNELING_IN_SECONDS / magmaBlocks.size();
        final int magmaCooldownInTicks = GameState.secondToTick(EXPLOSION_MAGMA_CHANNELING_IN_SECONDS + EXPLOSION_LAVA_CHANNELING_IN_SECONDS + EXPLOSION_LAVA_DURATION_IN_SECONDS);
        double magmaDelayInSeconds = 0.0;
        for (Block baseBlock : magmaBlocks) {

            //turn into magma block, then cooldown to stone
            Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {
                baseBlock.setType(Material.MAGMA_BLOCK);
                Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> baseBlock.setType(Material.STONE), magmaCooldownInTicks);
            }, GameState.secondToTick(magmaDelayInSeconds));

            magmaDelayInSeconds += magmaChannelingInSeconds;
        }

        //create the closer lava blocks first
        Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {

            //apply BakuretsuMahou damage to nearby living entities
            var entities = world.getNearbyEntities(explosionOrigin, EXPLOSION_DAMAGE_RADIUS, EXPLOSION_DAMAGE_RADIUS, EXPLOSION_DAMAGE_HEIGHT);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).damage(EXPLOSION_DAMAGE, owner.getEntity());
                }
            }


            double lavaChannelingInSeconds = EXPLOSION_LAVA_CHANNELING_IN_SECONDS / lavaBlocks.size();
            double lavaDelayInSeconds = 0.0;
            int lavaDurationInTicks = GameState.secondToTick(EXPLOSION_LAVA_DURATION_IN_SECONDS);
            for (Block lavaBar : lavaBlocks) {

                //create lava first, then turn back into air
                Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {
                    lavaBar.setType(Material.LAVA);
                    world.playSound(lavaBar.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 0.1f);

                    Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> lavaBar.setType(Material.AIR), lavaDurationInTicks);
                }, GameState.secondToTick(lavaDelayInSeconds));

                lavaDelayInSeconds += lavaChannelingInSeconds;
            }
        }, GameState.secondToTick(EXPLOSION_MAGMA_CHANNELING_IN_SECONDS));
    }

    private void applyCost() {
        ManaComponent manaComponent = (ManaComponent) owner.getComponent(ManaComponent.class);
        manaComponent.consumeMana(manaComponent.getMaxMana() * manaPercentCost);
        cooldownComponent.restartTimer();
    }
}

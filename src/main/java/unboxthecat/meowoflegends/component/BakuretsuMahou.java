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
import org.bukkit.scheduler.BukkitRunnable;
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
    public static final int EXPLOSION_TARGET_REACH = 200;
    public static final double COOLDOWN_IN_SECONDS = 3.0;
    public static final double MANA_PERCENT_COST = 0.05;

    public static final int EXPLOSION_MAGMA_DEPTH = 1;
    public static final int EXPLOSION_MAGMA_HEIGHT = 1;
    public static final int EXPLOSION_RING_COUNT = 6;
    public static final double EXPLOSION_CHANNELING_IN_SECONDS = 5.0;
    public static final double EXPLOSION_MIN_RADIUS = 20.0;
    public static final double EXPLOSION_MAX_RADIUS = 30.0;
    public static final double EXPLOSION_HEIGHT = 50.0;
    public static final double EXPLOSION_POWER = 200.0;
    public static final int EXPLOSION_FIRE_TICK = GameState.secondToTick(5.0);


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
        cooldownComponent.onAttach(this.owner);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner) {
        HandlerList.unregisterAll(this);
        cooldownComponent.onRemove(this.owner);
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
        ManaComponent manaComponent = owner.getComponent(ManaComponent.class);
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
        ArrayList<Block> magmaBlocks = Geometric.getBlockInCylinder(explosionOrigin, (int) EXPLOSION_MAX_RADIUS, EXPLOSION_MAGMA_DEPTH, EXPLOSION_MAGMA_HEIGHT,
                b -> b.getType().isSolid() && b.getType().getHardness() >= 0.0);
        Collections.shuffle(magmaBlocks);

        //playing channeling sound
        world.playSound(owner.getEntity().getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 2.0f, 0.5f);
        world.playSound(explosionOrigin, Sound.BLOCK_LAVA_AMBIENT, 1.0f, 0.5f);

        //create magma block base
        double magmaChannelingInSeconds = EXPLOSION_CHANNELING_IN_SECONDS / magmaBlocks.size();
        final int magmaCooldownInTicks = GameState.secondToTick(EXPLOSION_CHANNELING_IN_SECONDS);
        double magmaDelayInSeconds = 0.0;
        for (Block baseBlock : magmaBlocks) {

            //turn into magma block, then cooldown to stone
            Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {
                baseBlock.setType(Material.MAGMA_BLOCK);
                Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {
                    if (baseBlock.getType().isSolid()) {
                        baseBlock.setType(Material.STONE);
                    }
                }, magmaCooldownInTicks);
            }, GameState.secondToTick(magmaDelayInSeconds));

            magmaDelayInSeconds += magmaChannelingInSeconds;
        }

        //spawn ring
        double ringChannelingInSeconds = EXPLOSION_CHANNELING_IN_SECONDS / EXPLOSION_RING_COUNT;
        for (int ring = 0; ring < EXPLOSION_RING_COUNT; ++ring) {
            double ringRadius = new Random().nextDouble(EXPLOSION_MIN_RADIUS, EXPLOSION_MAX_RADIUS);
            double y = explosionOrigin.getY() + EXPLOSION_HEIGHT / EXPLOSION_RING_COUNT * ring;

            int spawnDelayInTicks = GameState.secondToTick(ringChannelingInSeconds * ring);
            BukkitRunnable spawningRing = new BukkitRunnable() {
                int currentTick = 0;
                final int endingTick = GameState.secondToTick(EXPLOSION_CHANNELING_IN_SECONDS) - spawnDelayInTicks;
                @Override
                public void run() {
                    //spawn ring particle
                    for (double radian = 0.0; radian <= 2 * Math.PI; radian += 0.05) {
                        double x = ringRadius * Math.cos(radian) + explosionOrigin.getX();
                        double z = ringRadius * Math.sin(radian) + explosionOrigin.getZ();
                        world.spawnParticle(Particle.DRIP_LAVA, x, y, z, 30, 1.0, 0.0, 1.0);
                        world.spawnParticle(Particle.DRIPPING_OBSIDIAN_TEAR, x, y, z, 30, 1.0, 0.0, 1.0);
                        world.spawnParticle(Particle.ENCHANTMENT_TABLE, x, y, z, 30, 1.0, 0.0, 1.0);
                    }

                    currentTick += 5;
                    if (currentTick >= endingTick) {
                        this.cancel();
                    }
                }
            };
            spawningRing.runTaskTimer(GameState.getPlugin(), spawnDelayInTicks, 5L);
        }


        Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {

            //play explosion sound
            world.playSound(explosionOrigin, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.1f);

            //apply BakuretsuMahou effect to nearby living entities
            var entities = world.getNearbyEntities(explosionOrigin, EXPLOSION_MAX_RADIUS, EXPLOSION_HEIGHT, EXPLOSION_MAX_RADIUS);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.setFireTicks(livingEntity.getFireTicks() + EXPLOSION_FIRE_TICK);
                }
            }
            //https://minecraft.fandom.com/wiki/Explosion#Explosion_strength
            world.createExplosion(explosionOrigin, (float) EXPLOSION_POWER, true, true, owner.getEntity());

            //draw explosion bar
            BukkitRunnable spawningRing = new BukkitRunnable() {
                int currentTick = 0;
                final int endingTick = GameState.secondToTick(EXPLOSION_CHANNELING_IN_SECONDS);
                @Override
                public void run() {
                    //spawn center bar
                    for(double y = 0.0; y <= EXPLOSION_HEIGHT; ++y) {
                        world.spawnParticle(Particle.LAVA, explosionOrigin.getX(), explosionOrigin.getY() + y, explosionOrigin.getZ(), 200, 5.0, 0.5, 5.0);
                    }

                    currentTick += 1;
                    if (currentTick >= endingTick) {
                        this.cancel();
                    }
                }
            };
            spawningRing.runTaskTimer(GameState.getPlugin(), 0L, 1L);
        }, GameState.secondToTick(EXPLOSION_CHANNELING_IN_SECONDS));
    }

    private void applyCost() {
        ManaComponent manaComponent = owner.getComponent(ManaComponent.class);
        manaComponent.consumeMana(manaComponent.getMaxMana() * manaPercentCost);
        cooldownComponent.restartTimer();
    }
}

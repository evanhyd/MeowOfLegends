package unboxthecat.meowoflegends.component.ability.megumin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
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
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.TimerComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.utility.Geometric;

import java.util.*;

public class BakuretsuMahou extends AbilityComponent implements Listener {
    private MOLEntity owner;
    private final TimerComponent cooldown;
    private ManaComponent manaView;

    public BakuretsuMahou() {
        super(true);
        this.cooldown = new TimerComponent();
    }

    public BakuretsuMahou(Map<String, Object> data) {
        super(true);
        this.cooldown = (TimerComponent)(data.get("cooldown"));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("cooldown", cooldown);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner) {
        setUpAbilitySlot(owner);
        this.owner = owner;
        this.cooldown.onAttach(this.owner);
        this.manaView = Objects.requireNonNull(this.owner.getComponent(ManaComponent.class));
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner) {
        HandlerList.unregisterAll(this);
        this.cooldown.onRemove(this.owner);
        this.owner = null;
    }

    @EventHandler
    public void trigger(PlayerInteractEvent event) {
        if (isOwner(event.getPlayer()) &&
            isUsingAbilitySlot(event.getPlayer()) &&
            isUsingBlazeRod(event.getAction()) &&
            isLookingAtSolidBlock() &&
            isManaSufficient() &&
            isCooldownReady()) {

            applyAbilityCost();
            explode();
        }
    }

    private boolean isOwner(Entity user) {
        return user == owner.getEntity();
    }

    private boolean isUsingBlazeRod(Action action) {
        return ((HumanEntity)owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD &&
               (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean isLookingAtSolidBlock() {
        Set<Material> ignoredBlockType = Set.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER, Material.LAVA);
        return ((HumanEntity)owner.getEntity()).getTargetBlock(ignoredBlockType, getAbilityReach()).getType().isSolid();
    }

    private boolean isManaSufficient() {
        return this.manaView.getMana() >= getAbilityManaCost();
    }

    private boolean isCooldownReady() {
        return cooldown.isReady();
    }

    private void explode() {
        final int EXPLOSION_MAGMA_DEPTH = 1;
        final int EXPLOSION_MAGMA_HEIGHT = 1;
        final int EXPLOSION_RING_COUNT = 6;
        final double EXPLOSION_CHANNELING_IN_SECONDS = 5.0;


        //get explosion center
        Set<Material> ignoredBlockType = Set.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER, Material.LAVA);
        Block explosionBlock = ((LivingEntity)owner.getEntity()).getTargetBlock(ignoredBlockType, getAbilityReach());
        World world = explosionBlock.getWorld();
        Location explosionOrigin = explosionBlock.getLocation();

        //prefetch the blocks first, so the animation looks less laggy
        ArrayList<Block> magmaBlocks = Geometric.getBlockInCylinder(
                explosionOrigin, (int) getAbilityExplosionRadius(), EXPLOSION_MAGMA_DEPTH, EXPLOSION_MAGMA_HEIGHT,
                b -> b.getType().isSolid() && b.getType().getHardness() >= 0.0
        );
        Collections.shuffle(magmaBlocks);

        //playing channeling sound
        world.playSound(owner.getEntity().getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 2.0f, 0.5f);
        world.playSound(explosionOrigin, Sound.BLOCK_LAVA_AMBIENT, 1.0f, 0.5f);

        //create magma block base
        final double magmaChannelingInSeconds = EXPLOSION_CHANNELING_IN_SECONDS / magmaBlocks.size();
        final long magmaCooldownInTicks = GameState.secondToTick(EXPLOSION_CHANNELING_IN_SECONDS);
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

        //spawn ring particles
        double ringChannelingInSeconds = EXPLOSION_CHANNELING_IN_SECONDS / EXPLOSION_RING_COUNT;
        for (int ring = 0; ring < EXPLOSION_RING_COUNT; ++ring) {
            double ringRadius = new Random().nextDouble(getAbilityExplosionRadius() * 0.8, getAbilityExplosionRadius() * 1.2);
            double y = explosionOrigin.getY() + getAbilityExplosionHeight() / EXPLOSION_RING_COUNT * ring;

            long spawnDelayInTicks = GameState.secondToTick(ringChannelingInSeconds * ring);
            BukkitRunnable spawningRing = new BukkitRunnable() {
                long currentTick = 0;
                final long endingTick = GameState.secondToTick(EXPLOSION_CHANNELING_IN_SECONDS) - spawnDelayInTicks;
                @Override
                public void run() {
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


        //create explosion
        Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {

            //play explosion sound
            world.playSound(explosionOrigin, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.1f);

            //apply BakuretsuMahou effect to nearby living entities
            world.getNearbyEntities(explosionOrigin, getAbilityExplosionRadius(), getAbilityExplosionHeight(), getAbilityExplosionRadius()).stream()
                    .filter(entity -> entity instanceof LivingEntity)
                    .forEach(entity -> entity.setFireTicks((int) (entity.getFireTicks() + getAbilityFireTick())));

            //create explosion
            world.createExplosion(explosionOrigin, getAbilityExplosionPower(), true, true, owner.getEntity());

        }, GameState.secondToTick(EXPLOSION_CHANNELING_IN_SECONDS));
    }

    private void applyAbilityCost() {
        this.manaView.consumeMana(getAbilityManaCost());
        this.cooldown.countDown(getAbilityCooldown());
    }

    private int getAbilityReach() {
        final int EXPLOSION_TARGET_REACH = 200;
        return EXPLOSION_TARGET_REACH;
    }
    private double getAbilityCooldown() {
        final double BASE_COOLDOWN_IN_SECONDS = GameState.tickToSecond(24000);
        final double COOLDOWN_REDUCE_PERCENTAGE = 0.995;
        final int level = (owner.getEntity() instanceof Player player) ? player.getLevel() : 0;
        return (BASE_COOLDOWN_IN_SECONDS * Math.pow(COOLDOWN_REDUCE_PERCENTAGE, level));
    }

    private double getAbilityManaCost() {
        final double MANA_PERCENT_COST = 0.15;
        return manaView.getMaxMana() * MANA_PERCENT_COST;
    }

    public long getAbilityFireTick() {
        final long EXPLOSION_FIRE_TICK = GameState.secondToTick(5.0);
        return EXPLOSION_FIRE_TICK;
    }

    private float getAbilityExplosionPower() {
        //https://minecraft.fandom.com/wiki/Explosion#Explosion_strength
        final float EXPLOSION_POWER = 150.0F;
        final int level = (owner.getEntity() instanceof Player player) ? player.getLevel() : 0;
        return EXPLOSION_POWER + level;
    }

    private double getAbilityExplosionRadius() {
        return getAbilityExplosionPower() / 10.0;
    }

    private double getAbilityExplosionHeight() {
        return getAbilityExplosionPower() / 5.0;
    }

    @Override
    public String toString() {
        return super.toString() + cooldown.toString();
    }
}

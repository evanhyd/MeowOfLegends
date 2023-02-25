package unboxthecat.meowoflegends.component.megumin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.utility.Geometric;

import java.util.*;

public class BakuretsuMahou extends AbilityComponent implements Listener {
  private MOLEntity owner;
  private ManaComponent manaView;

  public BakuretsuMahou(int abilitySlot) {
    super(abilitySlot);
  }

  public BakuretsuMahou(Map<String, Object> data) {
    super(data);
  }

  @Override
  public @NotNull Map<String, Object> serialize() {
    return super.serialize();
  }

  @Override
  public void onAttach(MOLEntity owner, Object... objects) {
    super.onAttach(owner, objects);
    this.owner = owner;
    this.manaView = Objects.requireNonNull(owner.getComponent(ManaComponent.class));
    Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
  }

  @Override
  public void onRemove(MOLEntity owner, Object... objects) {
    HandlerList.unregisterAll(this);
    this.manaView = null;
    this.owner = null;
    super.onRemove(owner, objects);
  }
  @Override
  protected String getName() {
    return "BakuretsuMahou";
  }

  @Override
  protected int getMaxLevel() {
    return 2;
  }

  @Override
  public double getCooldown() {
    final double[] COOLDOWN = {20.0 * 60, 15.0 * 60, 10.0 * 60};
    return COOLDOWN[getLevel()];
  }

  @Override
  protected int getMaxCastToken() {
    return 1;
  }

  public final int getReach() {
    final int[] REACH = {200, 250, 300};
    return REACH[getLevel()];
  }

  public final double getManaCost() {
    final double[] MANA_COST = {200.0, 400.0, 800.0};
    return MANA_COST[getLevel()];
  }

  public final long getFireTicks() {
    final double[] FIRE_TICKS = {3.0, 4.0, 5.0};
    return GameState.secondToTick(FIRE_TICKS[getLevel()]);
  }

  public final float getExplosionPower() {
    //https://minecraft.fandom.com/wiki/Explosion#Explosion_strength
    final float[] EXPLOSION_POWER = {150.0f, 175.0f, 200.0f};
    return EXPLOSION_POWER[getLevel()];
  }

  public final double getExplosionRadius() {
    return getExplosionPower() / 10.0;
  }

  public final double getExplosionHeight() {
    return getExplosionPower() / 5.0;
  }

  @Override
  protected void castAbilityBegin() {
    manaView.consumeMana(getManaCost());
  }

  @Override
  protected void castAbilityImplementation() {
    final int EXPLOSION_MAGMA_DEPTH = 1;
    final int EXPLOSION_MAGMA_HEIGHT = 1;
    final int EXPLOSION_RING_COUNT = 6;
    final double EXPLOSION_CHANNELING_IN_SECONDS = 5.0;

    //get explosion center
    Set<Material> ignoredBlockType = Set.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER, Material.LAVA);
    Block explosionBlock = ((LivingEntity) owner.getEntity()).getTargetBlock(ignoredBlockType, getReach());
    World world = explosionBlock.getWorld();
    Location explosionOrigin = explosionBlock.getLocation();

    //prefetch the blocks first, so the animation looks less laggy
    ArrayList<Block> magmaBlocks = Geometric.getBlockInCylinder(
      explosionOrigin, (int) getExplosionRadius(), EXPLOSION_MAGMA_DEPTH, EXPLOSION_MAGMA_HEIGHT,
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
      double ringRadius = new Random().nextDouble(getExplosionRadius() * 0.8, getExplosionRadius() * 1.2);
      double y = explosionOrigin.getY() + getExplosionHeight() / EXPLOSION_RING_COUNT * ring;

      long spawnDelayInTicks = GameState.secondToTick(ringChannelingInSeconds * ring);
      BukkitRunnable spawningRing = new BukkitRunnable() {
        final long endingTick = GameState.secondToTick(EXPLOSION_CHANNELING_IN_SECONDS) - spawnDelayInTicks;
        long currentTick = 0;

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
      world.getNearbyEntities(explosionOrigin, getExplosionRadius(), getExplosionHeight(), getExplosionRadius()).stream()
        .filter(entity -> entity instanceof LivingEntity)
        .forEach(entity -> entity.setFireTicks((int) (entity.getFireTicks() + getFireTicks())));

      //create explosion
      world.createExplosion(explosionOrigin, getExplosionPower(), true, true, owner.getEntity());

    }, GameState.secondToTick(EXPLOSION_CHANNELING_IN_SECONDS));
  }

  @Override
  protected void castAbilityEnd() {}

  @EventHandler
  public void trigger(PlayerInteractEvent event) {
    if (event.getPlayer() == owner.getEntity() &&
      isUsingSlot(event.getPlayer().getInventory().getHeldItemSlot()) &&
      isUsingBlazeRod(event.getAction()) &&
      isLookingAtSolidBlock() &&
      isManaSufficient() &&
      isReady()) {
      castAbility();
    }
  }

  private boolean isUsingBlazeRod(Action action) {
    return ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD &&
      (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
  }

  private boolean isLookingAtSolidBlock() {
    Set<Material> ignoredBlockType = Set.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.WATER, Material.LAVA);
    return ((HumanEntity) owner.getEntity()).getTargetBlock(ignoredBlockType, getReach()).getType().isSolid();
  }

  private boolean isManaSufficient() {
    return this.manaView.getMana() >= getManaCost();
  }

  @Override
  public String toString() {
    return super.toString();
  }
}
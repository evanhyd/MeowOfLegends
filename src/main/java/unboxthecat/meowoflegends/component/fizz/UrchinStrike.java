package unboxthecat.meowoflegends.component.fizz;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.tag.fizz.IsUsingUrchinStrike;
import unboxthecat.meowoflegends.tag.fizz.SeaStoneTridentTag;
import unboxthecat.meowoflegends.utility.GameState;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class UrchinStrike extends AbilityComponent implements Listener {
  private MOLEntity owner;
  private ManaComponent manaView;
  private LivingEntity target;

  public UrchinStrike(int abilitySlot) {
    super(abilitySlot);
  }

  public UrchinStrike(Map<String, Object> data) {
    super(data);
  }

  @NotNull
  @Override
  public Map<String, Object> serialize() {
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
    return "Urchin Strike";
  }

  @Override
  protected int getMaxLevel() {
    return 2;
  }

  @Override
  protected double getCooldown() {
    final double[] COOLDOWN = {15.0, 12.0, 9.0};
    return COOLDOWN[getLevel()];
  }

  @Override
  protected int getMaxCastToken() {
    return 1;
  }

  protected double getManaCost() {
    final double[] MANA_COST = {30.0, 40.0, 50.0};
    return MANA_COST[getLevel()];
  }

  protected double getReach() {
    return 10.0;
  }

  protected double getDamage() {
    final double[] DAMAGE = {5.0, 7.0, 9.0};
    return DAMAGE[getLevel()];
  }

  @Override
  protected void castAbilityBegin() {
    manaView.consumeMana(getManaCost());
  }

  @Override
  protected void castAbilityImplementation() {
    HumanEntity humanEntity = (HumanEntity) owner.getEntity();
    humanEntity.getWorld().playSound(humanEntity.getLocation(), Sound.BLOCK_WATER_AMBIENT, 1.0f, 1.0f);

    Set<Entity> damaged = new HashSet<>();
    humanEntity.setGliding(true);
    humanEntity.setCollidable(false);
    owner.attachTag(new IsUsingUrchinStrike());

    final BukkitTask dashingTask = Bukkit.getScheduler().runTaskTimer(GameState.getPlugin(), () -> {
      humanEntity.getNearbyEntities(1.0, 1.0, 1.0).stream()
        .filter(entity -> entity instanceof LivingEntity)
        .filter(entity -> !damaged.contains(entity))
        .forEach(entity -> {
          ((LivingEntity) entity).damage(getDamage(), owner.getEntity());
          damaged.add(entity);
        });
    }, 0, 1);

    //what if other abilities make it glide?
    Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {
      dashingTask.cancel();
      owner.removeTag(IsUsingUrchinStrike.class);
      humanEntity.setCollidable(true);
      humanEntity.setGliding(false);
      humanEntity.setVelocity(new Vector());
      damaged.clear();
    }, GameState.secondToTick(0.5));

    humanEntity.setVelocity(humanEntity.getEyeLocation().getDirection().multiply(1.5));
  }

  @Override
  protected void castAbilityEnd() {
    target = null;
  }

  @EventHandler
  public void onUrchinStrikeAnimation(EntityToggleGlideEvent event) {
    if (event.getEntity() == owner.getEntity() &&
      owner.hasTag(IsUsingUrchinStrike.class)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  private void trigger(PlayerInteractEvent event) {
    if (event.getPlayer() == owner.getEntity() &&
      isUsingSlot(event.getPlayer().getInventory().getHeldItemSlot()) &&
      isUsingTrident(event.getAction()) &&
      isManaSufficient() &&
      isReady()) {
      target = (LivingEntity) getReachTarget();
      if (target != null) {
        castAbility();
      }
    }
  }

  private boolean isUsingTrident(Action action) {
    return ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.TRIDENT &&
      (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
  }

  private boolean isManaSufficient() {
    return manaView.getMana() >= getManaCost();
  }

  @Nullable
  private Entity getReachTarget() {
    final Predicate<Entity> selectLivingEntity = entity -> entity instanceof LivingEntity && entity != owner.getEntity();

    HumanEntity humanEntity = (HumanEntity) owner.getEntity();
    Location start = humanEntity.getEyeLocation();

    RayTraceResult hitTarget = humanEntity.getWorld().rayTrace(
      start, start.getDirection(), getReach(),
      FluidCollisionMode.NEVER, true, 1.0, selectLivingEntity
    );
    return hitTarget == null ? null : hitTarget.getHitEntity();
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

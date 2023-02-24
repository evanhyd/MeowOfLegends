package unboxthecat.meowoflegends.component.ability.fizz;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.tag.ability.fizz.SeaStoneTridentTag;
import unboxthecat.meowoflegends.utility.GameState;

import java.util.Map;
import java.util.Objects;
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
    final double[] COOLDOWN = {20.0, 15.0, 10.0};
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

  @Override
  protected void castAbilityBegin() {
    manaView.consumeMana(getManaCost());
  }

  @Override
  protected void castAbilityImplementation() {
    HumanEntity player = (HumanEntity) owner.getEntity();
    Vector direction = player.getEyeLocation().getDirection();

    if (direction.getY() * 4 > 1) {
      direction.setY((direction.getX() + direction.getZ()) / 8);
      direction.normalize();
    }

    //this doesn't do anything, since player's gravity if restore back to normal in the next few nano seconds
    //consider using Bukkit Task Scheduler
    player.setGravity(false);
    player.setVelocity(direction.multiply(4).clone());
    player.setGravity(true);

    //do damage
    target.damage(20.0, player);

    //on hit from SeaStoneTrident(fizz w)
    SeaStoneTridentTag tag = owner.getTag(SeaStoneTridentTag.class);
    if (tag != null) {
      target.damage(20.0, player);
      //set some effect
      target.setFireTicks(100);
    }
    owner.removeTag(SeaStoneTridentTag.class);
  }

  @Override
  protected void castAbilityEnd() {
    target = null;
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
    HumanEntity humanEntity = (HumanEntity) owner.getEntity();
    Vector direction = humanEntity.getEyeLocation().getDirection();
    Location startLocation = humanEntity.getEyeLocation().add(direction.multiply(0.5));

    Predicate<Entity> ignoreOwner = entity -> entity instanceof LivingEntity && entity != humanEntity;

    RayTraceResult hitTarget = humanEntity.getWorld().rayTrace(
      startLocation, direction, getReach(), FluidCollisionMode.NEVER, true, 1.0, ignoreOwner
    );
    return hitTarget == null ? null : hitTarget.getHitEntity();
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

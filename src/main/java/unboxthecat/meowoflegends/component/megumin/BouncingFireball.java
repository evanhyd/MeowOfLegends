package unboxthecat.meowoflegends.component.megumin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.utility.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BouncingFireball extends AbilityComponent implements Listener {
  private final Map<Projectile, Integer> fireballs;
  private MOLEntity owner;
  private ManaComponent manaView;

  public BouncingFireball(int abilitySlot) {
    super(abilitySlot);
    this.fireballs = new HashMap<>();
  }

  public BouncingFireball(Map<String, Object> data) {
    super(data);
    this.fireballs = new HashMap<>();
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
    this.fireballs.forEach((fireball, bouncedTime) -> fireball.remove());
    this.fireballs.clear();
    super.onRemove(owner, objects);
  }

  @Override
  protected String getName() {
    return "Bouncing Fireball";
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
    return 3;
  }

  private double getManaCost() {
    final double[] MANA_COST = {20.0, 30.0, 40.0};
    return MANA_COST[getLevel()];
  }

  private int getMaxBounce() {
    final int[] MAX_BOUNCE = {2, 4, 6};
    return MAX_BOUNCE[getLevel()];
  }

  private float getExplosionPower() {
    //https://minecraft.fandom.com/wiki/Explosion#Explosion_strength
    final float[] EXPLOSION_POWER = {3.0f, 4.0f, 5.0f};
    return EXPLOSION_POWER[getLevel()];
  }

  @Override
  protected void castAbilityBegin() {
    manaView.consumeMana(getManaCost());
  }

  @Override
  protected void castAbilityImplementation() {
    LivingEntity user = (LivingEntity) owner.getEntity();
    Location launchingLocation = user.getEyeLocation().add(user.getEyeLocation().getDirection());
    Fireball fireball = (Fireball) user.getWorld().spawnEntity(launchingLocation, EntityType.FIREBALL);
    fireball.setYield(getExplosionPower());
    fireball.setIsIncendiary(true);
    fireball.setDirection(user.getEyeLocation().getDirection());
    fireballs.put(fireball, 0);
  }

  @Override
  protected void castAbilityEnd() {
  }

  @EventHandler
  public void onFireballHit(ProjectileHitEvent event) {
    if (fireballs.containsKey(event.getEntity())) {
      Fireball fireball = (Fireball) event.getEntity();
      int bounce = fireballs.get(fireball);
      fireballs.remove(fireball);

      if (event.getHitBlockFace() != null && bounce < getMaxBounce()) {
        Vector incoming = event.getEntity().getVelocity();
        Vector normal = event.getHitBlockFace().getDirection();
        Vector outgoing = incoming.add(normal.multiply(-2.0 * incoming.dot(normal)));

        fireball.setYield(0.0f);
        fireball.setIsIncendiary(false);

        fireball = (Fireball) owner.getEntity().getWorld().spawnEntity(fireball.getLocation(), EntityType.FIREBALL);
        fireball.setYield(getExplosionPower());
        fireball.setIsIncendiary(true);
        fireball.setDirection(outgoing);
        fireballs.put(fireball, bounce + 1);

      } else if (event.getHitEntity() == owner.getEntity()) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void trigger(PlayerInteractEvent event) {
    if (event.getPlayer() == owner.getEntity() &&
      isUsingSlot(event.getPlayer().getInventory().getHeldItemSlot()) &&
      isUsingBlazeRod(event.getAction()) &&
      isManaSufficient() &&
      isReady()) {
      castAbility();
    }
  }

  private boolean isUsingBlazeRod(Action action) {
    return ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD &&
      (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
  }

  private boolean isManaSufficient() {
    return this.manaView.getMana() >= getManaCost();
  }

  @Override
  public String toString() {
    return super.toString() + "\n" + "Lived Fireball: " + fireballs.size();
  }
}

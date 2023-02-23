package unboxthecat.meowoflegends.component.ability.megumin;

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
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.utility.GameState;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class BouncingFireball extends AbilityComponent implements Listener {
  private final CooldownComponent abilityCooldown;
  private final Map<Projectile, Integer> fireballs;
  private MOLEntity owner;
  private ManaComponent manaView;

  public BouncingFireball() {
    super(true);
    this.abilityCooldown = new CooldownComponent(BouncingFireball.class.getSimpleName());
    this.fireballs = new HashMap<>();
  }

  public BouncingFireball(Map<String, Object> data) {
    super(true);
    this.abilityCooldown = (CooldownComponent) data.get("abilityCooldown");
    this.fireballs = new HashMap<>();
  }

  @NotNull
  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> data = new TreeMap<>();
    data.put("abilityCooldown", abilityCooldown);
    return data;
  }

  @Override
  public void onAttach(MOLEntity owner, Object... objects) {
    setUpAbilitySlot(owner);
    this.owner = owner;
    this.manaView = Objects.requireNonNull(owner.getComponent(ManaComponent.class));
    this.abilityCooldown.onAttach(owner);
    Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
  }

  @Override
  public void onRemove(MOLEntity owner, Object... objects) {
    HandlerList.unregisterAll(this);
    this.abilityCooldown.onRemove(owner);
    this.manaView = null;
    this.owner = null;
    this.fireballs.forEach((fireball, bouncedTime) -> fireball.remove());
    this.fireballs.clear();
  }

  @EventHandler
  public void onPlayerRightClick(PlayerInteractEvent event) {
    if (event.getPlayer() == owner.getEntity() &&
      isUsingAbilitySlot(event.getPlayer()) &&
      isUsingBlazeRod(event.getAction()) &&
      isManaSufficient() &&
      abilityCooldown.isReady()) {

      applyAbilityCost();
      launchFireball();
    }
  }

  private boolean isUsingBlazeRod(Action action) {
    return ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD &&
      (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
  }

  private boolean isManaSufficient() {
    return this.manaView.getMana() >= getAbilityManaCost();
  }

  private void applyAbilityCost() {
    this.manaView.consumeMana(getAbilityManaCost());
    this.abilityCooldown.run(getAbilityCooldown(), false);
  }

  private void launchFireball() {
    LivingEntity user = (LivingEntity) owner.getEntity();
    Location launchingLocation = user.getEyeLocation().add(user.getEyeLocation().getDirection());
    Fireball fireball = (Fireball) user.getWorld().spawnEntity(launchingLocation, EntityType.FIREBALL);
    fireball.setYield(getAbilityExplosionPower());
    fireball.setIsIncendiary(true);
    fireball.setDirection(user.getEyeLocation().getDirection());
    fireballs.put(fireball, 0);
  }

  @EventHandler
  public void onFireballHit(ProjectileHitEvent event) {
    if (fireballs.containsKey(event.getEntity())) {
      Fireball fireball = (Fireball) event.getEntity();
      int bounce = fireballs.get(fireball);
      fireballs.remove(fireball);

      if (event.getHitBlockFace() != null && bounce < getAbilityMaxBounce()) {
        Vector incoming = event.getEntity().getVelocity();
        Vector normal = event.getHitBlockFace().getDirection();
        Vector outgoing = incoming.add(normal.multiply(-2.0 * incoming.dot(normal)));

        fireball.setYield(0.0f);
        fireball.setIsIncendiary(false);

        fireball = (Fireball) owner.getEntity().getWorld().spawnEntity(fireball.getLocation(), EntityType.FIREBALL);
        fireball.setYield(getAbilityExplosionPower());
        fireball.setIsIncendiary(true);
        fireball.setDirection(outgoing);
        fireballs.put(fireball, bounce + 1);

      } else if (event.getHitEntity() == owner.getEntity()) {
        event.setCancelled(true);
      }
    }
  }

  private double getAbilityManaCost() {
    return 20.0;
  }

  public double getAbilityCooldown() {
    return 10.0;
  }

  private int getAbilityMaxBounce() {
    return 2;
  }

  private float getAbilityExplosionPower() {
    return 5.0f;
  }

  @Override
  public String toString() {
    return super.toString() + "\n" + abilityCooldown.toString() + "\n" + "Lived Fireball: " + fireballs.size();
  }
}

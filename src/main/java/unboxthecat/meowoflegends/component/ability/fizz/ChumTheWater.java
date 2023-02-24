package unboxthecat.meowoflegends.component.ability.fizz;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.utility.GameState;

import java.util.Map;
import java.util.Objects;

public class ChumTheWater extends AbilityComponent implements Listener {
  private MOLEntity owner;
  private ManaComponent manaView;

  public ChumTheWater(int abilitySlot) {
    super(abilitySlot);
  }

  public ChumTheWater(Map<String, Object> data) {
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
    return "Chum The Water";
  }

  @Override
  protected int getMaxLevel() {
    return 2;
  }

  @Override
  protected double getCooldown() {
    final double[] COOLDOWN = {5.0 * 60, 4.0 * 60, 3.0 * 60};
    return COOLDOWN[getLevel()];
  }

  @Override
  protected int getMaxCastToken() {
    return 1;
  }

  protected double getManaCost() {
    final double[] MANA_COST = {200.0, 400.0, 800.0};
    return MANA_COST[getLevel()];
  }

  @Override
  protected void castAbilityBegin() {
    manaView.consumeMana(getMaxCastToken());
  }

  @Override
  protected void castAbilityImplementation() {
    Player player = (Player) owner.getEntity();

    /*
    Vector direction = player.getEyeLocation().getDirection();
    Location startLocation = player.getEyeLocation().add(direction.multiply(0.5));

    Entity fish = player.getWorld().spawnEntity(startLocation, EntityType.TROPICAL_FISH);
    fish.setVelocity(direction.multiply(10));

    Location endLocation = fish.getLocation();
    */

    if (!(owner.getEntity() instanceof LivingEntity)) {
      return;
    }

    Material weaponUsed = ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType();

    ((HumanEntity) owner.getEntity()).sendMessage("weapon used is not null and is " + weaponUsed.toString());

    int refreshCooldown = 0;
    ((HumanEntity) owner.getEntity()).setCooldown(weaponUsed, refreshCooldown);
  }

  @Override
  protected void castAbilityEnd() {}

  private boolean isManaSufficient() {
    return manaView.getMana() >= getManaCost();
  }

  private boolean isUsingTrident(Action action) {
    return ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.TRIDENT &&
      (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
  }

  @EventHandler
  private void trigger(PlayerInteractEvent event) {
    if (event.getPlayer() == owner.getEntity() &&
      isUsingSlot(event.getPlayer().getInventory().getHeldItemSlot()) &&
      isUsingTrident(event.getAction()) &&
      isManaSufficient() &&
      isReady()) {
      castAbility();
    }
  }
}

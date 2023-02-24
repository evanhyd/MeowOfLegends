package unboxthecat.meowoflegends.component.ability.fizz;

import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.tag.ability.fizz.SeaStoneTridentTag;

import java.util.Map;

//todo: proper damage and mana scaling for abilities
public class SeaStoneTrident extends AbilityComponent implements Listener {
  private MOLEntity owner;
  private ManaComponent manaView;

  public SeaStoneTrident(int abilitySlot) {
    super(abilitySlot);
  }

  public SeaStoneTrident(Map<String, Object> data) {
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
    this.manaView = owner.getComponent(ManaComponent.class);
    assert (manaView != null);
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
    return "Sea Stone Trident";
  }

  @Override
  protected int getMaxLevel() {
    return 2;
  }

  @Override
  protected double getCooldown() {
    final double[] COOLDOWN = {8.0, 6.0, 4.0};
    return COOLDOWN[getLevel()];
  }

  public final double getManaCost() {
    final double[] MANA_COST = {10.0, 15.0, 20.0};
    return MANA_COST[getLevel()];
  }

  @Override
  protected int getMaxCastToken() {
    return 1;
  }

  @Override
  protected void castAbilityBegin() {
    manaView.consumeMana(getManaCost());
  }

  @Override
  protected void castAbilityImplementation() {
    //attach tag for ability activation
    owner.attachTag(new SeaStoneTridentTag());

    //refresh attack cooldown(not working)
    Material weaponUsed = ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType();
    int refreshCooldown = 0;
    ((HumanEntity) owner.getEntity()).setCooldown(weaponUsed, refreshCooldown);

    //remove ability after 4s
    Bukkit.getScheduler().scheduleSyncDelayedTask(
      GameState.getPlugin(),
      () -> owner.removeTag(SeaStoneTridentTag.class),
      GameState.secondToTick(4.0)
    );
  }

  @Override
  protected void castAbilityEnd() {}

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

  private boolean isUsingTrident(Action action) {
    return ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.TRIDENT &&
      (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
  }

  private boolean isManaSufficient() {
    return manaView.getMana() >= getManaCost();
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

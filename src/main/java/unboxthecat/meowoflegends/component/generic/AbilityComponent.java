package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.utility.Timer;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public abstract class AbilityComponent implements MOLComponent {
  public interface CooldownCallback {
    void run();
  }

  private final int slot;
  private int level;
  private int castToken;

  //cooldown timer
  private final Timer displayTimer;
  private final Timer cooldownTimer;

  protected AbilityComponent(int abilitySlot) {
    slot = abilitySlot;
    level = 0;
    castToken = 1;
    displayTimer = new Timer();
    cooldownTimer = new Timer();
  }

  protected AbilityComponent(Map<String, Object> data) {
    slot = (int) data.get("slot");
    level = (int) data.get("level");
    castToken = (int) data.get("castToken");
    displayTimer = (Timer) data.get("displayTimer");
    cooldownTimer = (Timer) data.get("cooldownTimer");
  }

  @NotNull
  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> data = new TreeMap<>();
    data.put("slot", slot);
    data.put("level", level);
    data.put("castToken", castToken);
    data.put("displayTimer", displayTimer);
    data.put("cooldownTimer", cooldownTimer);
    return data;
  }

  @Override
  public void onAttach(MOLEntity owner, Object... objects) {
    CooldownCallback cooldownCallback = (objects.length > 0) ? (CooldownCallback) objects[0] : () -> {};
    Player player = (owner.getEntity() instanceof Player p) ? p : null;

    //display cooldown on the scoreboard
    if (player != null) {
      displayTimer.setCallback(() -> {
        String content = String.format("[%d/%d] %.1f", castToken, getMaxCastToken(), cooldownTimer.getRemainingSeconds());
        Objects.requireNonNull(owner.getComponent(StatsBoardComponent.class)).set(getName(), content);
      });
      displayTimer.resume();
    }

    //execute actual cooldown timer
    cooldownTimer.setCallback(() -> {
      if (player != null) {
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        displayTimer.pause();
        Objects.requireNonNull(owner.getComponent(StatsBoardComponent.class)).remove(getName());
      }
      cooldownCallback.run();

      //charge until the cast tokens are full
      if (++castToken < getMaxCastToken()) {
        activateCooldown();
      }
    });

    //recharge tokens when loading for the first time
    if (cooldownTimer.isIdling()) {
      castToken = getMaxCastToken();
    } else {
      cooldownTimer.resume();
    }
  }

  @Override
  public void onRemove(MOLEntity owner, Object... objects) {
    cooldownTimer.pause();
    displayTimer.pause();
  }

  /**
   * @return true if the ability is ready to use.
   */
  protected final boolean isReady() {
    return castToken > 0;
  }

  protected final boolean isUsingSlot(int abilitySlot) {
    return slot == abilitySlot;
  }

  protected final int getSlot() {
    return slot;
  }

  protected final int getLevel() {
    return level;
  }

  protected final int getCastToken() {
    return castToken;
  }

  protected final double getRemainingCooldown() {
    return cooldownTimer.getRemainingSeconds();
  }

  protected abstract String getName();

  protected abstract int getMaxLevel();

  protected abstract double getCooldown();

  protected abstract int getMaxCastToken();

  /**
   * Put the ability into cooldown state.
   * The ability will continue to recharge until it has max cast tokens.
   */
  private void activateCooldown() {
    final double cooldown = getCooldown();
    final long tick = GameState.secondToTick(cooldown);

    //ability with no cooldown does not trigger timer
    if (tick == 0) {
      castToken = getMaxCastToken();
    } else if(cooldownTimer.isIdling()) {
      //trigger timer if not already running
      //for example, cast Bouncing Fireball twice in a row should not start another timer
      displayTimer.run(GameState.tickToSecond(1), true);
      cooldownTimer.run(cooldown, false);
    }
  }

  /**
   * Cast this ability.
   * Apply cooldown state and cost cast token afterward.
   */
  protected final void castAbility() {
    --castToken;
    activateCooldown();

    castAbilityBegin();
    castAbilityImplementation();
    castAbilityEnd();
  }

  /**
   * castAbility() has 3 phases.
   * Begin and End are useful for loading resources.
   * Implementation is for the actual implementation.
   */
  protected abstract void castAbilityBegin();
  protected abstract void castAbilityImplementation();
  protected abstract void castAbilityEnd();

  @Override
  public String toString() {
    return String.format(
      "Name: %s\n" +
      "Level: %d / %d\n" +
      "Token: %d / %d\n" +
      "Cooldown: %.1f / %.1f",
      getName(), level, getMaxLevel(), castToken, getMaxCastToken(), cooldownTimer.getRemainingSeconds(), getCooldown());
  }
}
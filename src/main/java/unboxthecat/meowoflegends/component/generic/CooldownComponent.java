package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.base.MOLComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.utility.Timer;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class CooldownComponent implements MOLComponent {
  public interface Callback {
    void run();
  }

  private final String title;
  private final Timer display;
  private final Timer cooldown;
  private Callback callback;

  public CooldownComponent(String title) {
    this.title = title;
    this.display = new Timer();
    this.cooldown = new Timer();
    this.callback = () -> {};
  }

  public CooldownComponent(Map<String, Object> data) {
    this.title = data.get("title").toString();
    this.display = (Timer) data.get("display");
    this.cooldown = (Timer) data.get("cooldown");
    this.callback = () -> {};
  }

  @NotNull
  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> data = new TreeMap<>();
    data.put("title", title);
    data.put("display", display);
    data.put("cooldown", cooldown);
    return data;
  }

  @Override
  public void onAttach(MOLEntity owner, Object... objects) {
    if (objects.length > 0) {
      this.callback = (Callback) objects[0];
    }

    final boolean isPlayer = owner.getEntity() instanceof Player;

    if (isPlayer) {
      this.display.setCallback(() -> {
        Objects.requireNonNull(owner.getComponent(StatsBoardComponent.class))
          .set(title, String.format("%.1f", this.cooldown.getRemainingSeconds()));
      });
      this.display.resume();
    }

    this.cooldown.setCallback(() -> {
      if (isPlayer) {
        Player player = (Player) owner.getEntity();
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        display.pause();
        Objects.requireNonNull(owner.getComponent(StatsBoardComponent.class)).remove(title);
      }
      callback.run();
    });
    this.cooldown.resume();
  }

  @Override
  public void onRemove(MOLEntity owner, Object... objects) {
    this.cooldown.pause();
    this.display.pause();
    this.callback = () -> {};
  }

    /**
     * Put the ability into cooldown.
     * @param cooldownInSeconds ability cooldown.
     * @param shouldRepeat true if the cooldown should repeat infinitely.
     */
  public void run(double cooldownInSeconds, boolean shouldRepeat) {
    display.run(GameState.tickToSecond(1), true);
    cooldown.run(cooldownInSeconds, shouldRepeat);
  }

    /**
     * @return true if the ability is ready to use.
     */
  public boolean isReady() {
    return cooldown.isIdling();
  }

  @Override
  public String toString() {
    return cooldown.toString();
  }
}

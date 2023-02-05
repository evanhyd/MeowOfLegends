package unboxthecat.meowoflegends.component.generic;

import org.bukkit.scheduler.BukkitRunnable;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.MOLComponent;
import unboxthecat.meowoflegends.entity.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class CooldownComponent implements MOLComponent {
    private class CooldownTimerTask extends BukkitRunnable {
        @Override
        public void run() {
            currentCooldown = Math.max(0, currentCooldown - GameState.secondToTick(1.0));
            if (currentCooldown == 0) {
                this.cancel();
                isCounting = false;
            }
        }
    }
    private int currentCooldown;
    private final int maxCooldown;
    private boolean isCounting;
    private final CooldownTimerTask timer;

    public CooldownComponent(int currentCooldown, int maxCooldown) {
        this.currentCooldown = currentCooldown;
        this.maxCooldown = maxCooldown;
        this.isCounting = false;
        this.timer = new CooldownTimerTask();
    }

    public CooldownComponent(Map<String, Object> data) {
        this.currentCooldown = (int) data.get("currentCooldown");
        this.maxCooldown = (int) data.get("maxCooldown");
        this.isCounting = Boolean.parseBoolean((String) data.get("isCounting"));
        this.timer = new CooldownTimerTask();
    }

    @Override
    public void onAttach(MOLEntity owner) {
        if (isCounting) {
            timer.runTaskTimerAsynchronously(GameState.getPlugin(), 0, GameState.secondToTick(1.0));
        }
    }

    @Override
    public void onRemove(MOLEntity owner) {
        stopCooldownTimer();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("currentCooldown", currentCooldown);
        data.put("maxCooldown", maxCooldown);
        data.put("isCounting", isCounting);
        return data;
    }

    public int getCurrentCooldownInTick() {
        return currentCooldown;
    }

    public int getMaxCooldownInTick() {
        return maxCooldown;
    }

    public void startCooldownTimer() {
        if (!isCounting) {
            isCounting = true;
            timer.runTaskTimerAsynchronously(GameState.getPlugin(), 0, GameState.secondToTick(1.0));
        }
    }

    public void stopCooldownTimer() {
        if (isCounting) {
            timer.cancel();
            isCounting = false;
        }
    }
}

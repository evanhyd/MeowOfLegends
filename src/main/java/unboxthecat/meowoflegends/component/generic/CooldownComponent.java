package unboxthecat.meowoflegends.component.generic;

import unboxthecat.meowoflegends.component.MOLComponent;
import unboxthecat.meowoflegends.entity.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class CooldownComponent implements MOLComponent {
    private final long cooldown;
    private long lastActivateTime;
    private long pausedTime;
    private boolean isPaused;

    public CooldownComponent(double cooldown) {
        this.cooldown = (long)cooldown * 1000L;
        lastActivateTime = 0L;
        pausedTime = 0L;
        isPaused = false;
    }

    public CooldownComponent(Map<String, Object> data) {
        cooldown = (long) data.get("cooldownInMillis");
        lastActivateTime = (long) data.get("lastActivateTime");
        pausedTime = (long)data.get("pausedTime");
        isPaused = (boolean) data.get("isPaused");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("cooldownInMillis", cooldown);
        data.put("lastActivateTime", lastActivateTime);
        data.put("pausedTime", pausedTime);
        data.put("isPaused", isPaused);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner) {
        if (isPaused) {
            long timeElapsedFromAFK = System.currentTimeMillis() - pausedTime;
            lastActivateTime += timeElapsedFromAFK;
            isPaused = false;
        }
    }

    @Override
    public void onRemove(MOLEntity owner) {
        pausedTime = System.currentTimeMillis();
        isPaused = true;
    }

    public boolean isReady() {
        return lastActivateTime + cooldown < System.currentTimeMillis();
    }

    public long getCooldownInMilli() {
        return Math.max(0L, cooldown - (System.currentTimeMillis() - lastActivateTime));
    }

    public void restartTimer() {
        lastActivateTime = System.currentTimeMillis();
    }
}

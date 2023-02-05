package unboxthecat.meowoflegends.component.generic;

import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class CooldownComponent implements MOLComponent {
    private final long cooldown;
    private long lastActivateTime;

    public CooldownComponent(double cooldown) {
        this.cooldown = (long)cooldown * 1000L;
        lastActivateTime = 0L;
    }

    public CooldownComponent(Map<String, Object> data) {
        cooldown = ((Integer)data.get("cooldownInMillis")).longValue();
        lastActivateTime = ((Integer) data.get("lastActivateTime")).longValue();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("cooldownInMillis", cooldown);
        data.put("lastActivateTime", lastActivateTime);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner) {}

    @Override
    public void onRemove(MOLEntity owner) {}

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

package unboxthecat.meowoflegends.component.generic;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.base.MOLComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class TimerComponent implements MOLComponent {
    public interface TimerCallback {
        void run();
    }

    private MOLEntity owner;
    private long remainingTicks;
    private BukkitTask task;

    public TimerComponent() {
        this.remainingTicks = 0;
        this.task = null;
    }

    public TimerComponent(Map<String, Object> data) {
        this.remainingTicks = Long.parseLong(data.get("remainingTicks").toString());
        this.task = null;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("remainingTicks", remainingTicks);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner) {
        this.owner = owner;
    }

    @Override
    public void onRemove(MOLEntity owner) {
        this.owner = null;
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void countDown(double seconds) {
        countDown(seconds, ()->{});
    }
    public void countDown(double seconds, TimerCallback callback) {
        if (task != null) {
            task.cancel();
        }

        remainingTicks = GameState.secondToTick(seconds);
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTicks <= 0) {
                    cancel();
                    task = null;
                    callback.run();
                }
                --remainingTicks;
            }
        }.runTaskTimerAsynchronously(GameState.getPlugin(), 0, 1L);
    }
    public boolean isReady() {
        return task == null;
    }
    private double getRemainingTime() {
        return GameState.tickToSecond(remainingTicks);
    }

    @Override
    public String toString(){
        return  "Remaining Time: " + this.getRemainingTime() + " s\n";
    }
}

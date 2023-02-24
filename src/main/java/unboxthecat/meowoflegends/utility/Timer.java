package unboxthecat.meowoflegends.utility;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

public class Timer implements ConfigurationSerializable {
    public interface Callback {
        void run();
    }

    private long period;
    private boolean repeat;
    private long remainingTicks;
    private boolean idle;
    private Callback callback;
    private BukkitTask task;

    public Timer() {
        this.period = 0;
        this.repeat = false;
        this.remainingTicks = 0;
        this.idle = true;
        this.callback = () -> {};
        this.task = null;
    }

    public Timer(Map<String, Object> data) {
        this.period = Long.parseLong(data.get("period").toString());
        this.repeat = Boolean.parseBoolean(data.get("repeat").toString());
        this.remainingTicks = Long.parseLong(data.get("remainingTick").toString());
        this.idle = Boolean.parseBoolean(data.get("idle").toString());
        this.callback = () -> {};
        this.task = null;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("period", period);
        data.put("repeat", repeat);
        data.put("remainingTick", remainingTicks);
        data.put("idle", idle);
        return data;
    }

    /**
     * Set the callback function to the timer.
     * The callback function is executed at the end of the timer period.
     * @param callback the callback function
     */
    public void setCallback(@NotNull Timer.Callback callback) {
        this.callback = callback;
    }

    /**
     * Abandon the previous task timer, and start a new timer with given configuration.
     * @param periodInSeconds timer period in seconds
     * @param shouldRepeat true if the period repeats
     */
    public void run(double periodInSeconds, boolean shouldRepeat) {
        if (task != null) {
            task.cancel();
        }
        remainingTicks = (period = GameState.secondToTick(periodInSeconds));
        assert remainingTicks != 0; //avoid a timer with no period
        repeat = shouldRepeat;
        runTaskTimer();
    }

    public void pause() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void resume() {
        if (!idle) {
            runTaskTimer();
        }
    }

    public double getRemainingSeconds() {
        return GameState.tickToSecond(remainingTicks);
    }

    public boolean isIdling() {
        return idle;
    }

    private void runTaskTimer() {
        idle = false;

        //callback may access spigot API, hence must be synchronized
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (--remainingTicks <= 0) {
                    if (repeat) {
                        remainingTicks = period;
                    } else {
                        cancel();
                        task = null;
                        idle = true;
                    }
                    callback.run();
                }
            }
        }.runTaskTimer(GameState.getPlugin(), 0, 1L);
    }

    @Override
    public String toString() {
        return String.format("Period: %d\nRepeat: %b\nRemaining Ticks: %d", period, repeat, remainingTicks);
    }
}

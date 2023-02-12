package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.base.MOLComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class TimerComponent implements MOLComponent, Listener {
    private MOLEntity owner;
    private long activationTime;
    private long pausedTime;
    private boolean isPaused;

    public TimerComponent() {
        this.activationTime = System.currentTimeMillis();
        this.pausedTime = System.currentTimeMillis();
        this.isPaused = false;
    }

    public TimerComponent(Map<String, Object> data) {
        this.activationTime = Long.parseLong(data.get("nextActivationTime").toString());
        this.pausedTime = Long.parseLong(data.get("pausedTime").toString());
        this.isPaused = Boolean.parseBoolean(data.get("isPaused").toString());
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("nextActivationTime", activationTime);
        data.put("pausedTime", pausedTime);
        data.put("isPaused", isPaused);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner) {
        this.owner = owner;
//        if (isPaused) {
//            activationTime += System.currentTimeMillis() - pausedTime;
//            isPaused = false;
//        }
//        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner) {
//        HandlerList.unregisterAll(this);
        this.owner = null;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer() == owner.getEntity()) {
            pausedTime = System.currentTimeMillis();
            isPaused = true;
        }
    }

    public void countDown(double seconds) {
        activationTime = System.currentTimeMillis() + (long)(seconds * 1000L);
    }
    public boolean isReady() {
        return activationTime <= System.currentTimeMillis();
    }
    private double getRemainingTimeInSeconds() {
        return Math.max(0L, activationTime - System.currentTimeMillis()) / 1000.0;
    }

    @Override
    public String toString(){
        return  "Remaining Time: " + this.getRemainingTimeInSeconds() + " s\n" +
                "Current Time: " + System.currentTimeMillis() / 1000.0 + " s\n" +
                "Next Activation Time: " + this.activationTime / 1000.0  + " s\n" +
                "Paused Time: " + this.pausedTime / 1000.0 + " s\n" +
                "Is Paused: " + this.isPaused + "\n";
    }
}

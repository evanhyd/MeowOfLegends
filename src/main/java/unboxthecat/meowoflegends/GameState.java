package unboxthecat.meowoflegends;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import unboxthecat.meowoflegends.entity.MOLEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameState implements Listener {
    private static final int SERVER_TICK_RATE = 20; //may need to dynamically capture it in the future

    public static int secondToTick(double seconds) {
        return Math.max(1, (int)(Math.round(seconds * (double)SERVER_TICK_RATE)));
    }

    public static double tickToSecond(int tick) {
        return (double)tick / 20.0;
    }

    public static Plugin getPlugin() {
        return MeowOfLegends.getPlugin(MeowOfLegends.class);
    }

    public static FileConfiguration getConfig() { return getPlugin().getConfig(); }

    public static Map<UUID, MOLEntity> getPlayers() {
        return players;
    }

    private static final Map<UUID, MOLEntity> players = new HashMap<>();
}

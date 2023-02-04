package unboxthecat.meowoflegends;

import org.bukkit.plugin.Plugin;

public class GameState {

    private final static int SERVER_TICK_RATE = 20; //may need to dynamically capture it in the future

    public static long secondToTick(double seconds) {
        return Math.max(1L, Math.round(seconds * (double)(SERVER_TICK_RATE)));
    }

    public static Plugin getPlugin() {
        return MeowOfLegends.getPlugin(MeowOfLegends.class);
    }
}

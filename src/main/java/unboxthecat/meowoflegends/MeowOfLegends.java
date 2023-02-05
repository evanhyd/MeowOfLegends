package unboxthecat.meowoflegends;

import org.bukkit.plugin.java.JavaPlugin;
import unboxthecat.meowoflegends.handler.LoginMessageHandler;
import unboxthecat.meowoflegends.handler.MOLPlayerLoginHandler;
import unboxthecat.meowoflegends.handler.PlayerDeathMessageHandler;

public final class MeowOfLegends extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new MOLPlayerLoginHandler(), this);
        getServer().getPluginManager().registerEvents(new LoginMessageHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathMessageHandler(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
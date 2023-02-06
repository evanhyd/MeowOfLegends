package unboxthecat.meowoflegends;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import unboxthecat.meowoflegends.command.CommandDebug;
import unboxthecat.meowoflegends.command.CommandSetCurrentMana;
import unboxthecat.meowoflegends.command.CommandSetManaRegenerationRate;
import unboxthecat.meowoflegends.command.CommandSetMaxMana;
import unboxthecat.meowoflegends.component.BakuretsuMahou;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.component.generic.MOLComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.handler.LoginMessageHandler;
import unboxthecat.meowoflegends.handler.MOLPlayerLoginHandler;
import unboxthecat.meowoflegends.handler.PlayerDeathMessageHandler;
import unboxthecat.meowoflegends.tag.MOLTag;

public final class MeowOfLegends extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        ConfigurationSerialization.registerClass(MOLEntity.class);
        ConfigurationSerialization.registerClass(MOLComponent.class);
        ConfigurationSerialization.registerClass(MOLTag.class);
        ConfigurationSerialization.registerClass(AbilityComponent.class);
        ConfigurationSerialization.registerClass(CooldownComponent.class);
        ConfigurationSerialization.registerClass(ManaComponent.class);
        ConfigurationSerialization.registerClass(BakuretsuMahou.class);
        getServer().getPluginManager().registerEvents(new MOLPlayerLoginHandler(), this);
        getServer().getPluginManager().registerEvents(new LoginMessageHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathMessageHandler(), this);

        PluginCommand debugCommand = this.getCommand("debug");
        if(debugCommand != null) debugCommand.setExecutor(new CommandDebug());
        PluginCommand setCurrentManaCommand = this.getCommand("setcurrentmana");
        if(setCurrentManaCommand != null) setCurrentManaCommand.setExecutor(new CommandSetCurrentMana());
        PluginCommand setMaxManaCommand = this.getCommand("setmaxmana");
        if(setMaxManaCommand != null) setMaxManaCommand.setExecutor(new CommandSetMaxMana());
        PluginCommand setManaRegenerationRateCommand = this.getCommand("setmanaregenerationrate");
        if(setManaRegenerationRateCommand != null) setManaRegenerationRateCommand.setExecutor(new CommandSetManaRegenerationRate());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.saveConfig();
    }
}
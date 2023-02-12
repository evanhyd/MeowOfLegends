package unboxthecat.meowoflegends;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import unboxthecat.meowoflegends.command.*;
import unboxthecat.meowoflegends.component.megumin.BakuretsuMahou;
import unboxthecat.meowoflegends.component.fizz.UrchinStrike;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.HealthComponent;
import unboxthecat.meowoflegends.component.generic.TimerComponent;
import unboxthecat.meowoflegends.component.base.MOLComponent;
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
        ConfigurationSerialization.registerClass(TimerComponent.class);
        ConfigurationSerialization.registerClass(ManaComponent.class);
        ConfigurationSerialization.registerClass(HealthComponent.class);
        ConfigurationSerialization.registerClass(BakuretsuMahou.class);
        ConfigurationSerialization.registerClass(UrchinStrike.class);
        getServer().getPluginManager().registerEvents(new MOLPlayerLoginHandler(), this);
        getServer().getPluginManager().registerEvents(new LoginMessageHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathMessageHandler(), this);

        addCommand("debug", new CommandDebug());
        addCommand("manacomponent", new CommandManaComponent());
        addCommand("healthcomponent", new CommandHealthComponent());
        addCommand("top", new CommandTop());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.saveConfig();
    }

    public void addCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = this.getCommand(commandName);
        assert command != null;
        command.setExecutor(executor);;
    }
}
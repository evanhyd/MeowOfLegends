package unboxthecat.meowoflegends;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import unboxthecat.meowoflegends.command.*;
import unboxthecat.meowoflegends.component.BakuretsuMahou;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.component.generic.MOLComponent;
import unboxthecat.meowoflegends.component.ManaComponent;
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

        addCommand("debug", new CommandDebug());
        addCommand("manacomponent", new CommandManaComponent());
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
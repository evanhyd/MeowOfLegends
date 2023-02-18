package unboxthecat.meowoflegends;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import unboxthecat.meowoflegends.command.*;
import unboxthecat.meowoflegends.component.ability.fizz.SeaStoneTrident;
import unboxthecat.meowoflegends.component.ability.megumin.BakuretsuMahou;
import unboxthecat.meowoflegends.component.ability.megumin.BouncingFireball;
import unboxthecat.meowoflegends.component.ability.fizz.UrchinStrike;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.base.MOLComponent;
import unboxthecat.meowoflegends.component.generic.HealthComponent;
import unboxthecat.meowoflegends.component.generic.TimerComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.character.Megumin;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.handler.LoginMessageHandler;
import unboxthecat.meowoflegends.handler.MOLPlayerLoginHandler;
import unboxthecat.meowoflegends.handler.PlayerDeathMessageHandler;
import unboxthecat.meowoflegends.tag.ability.fizz.SeaStoneTridentTag;
import unboxthecat.meowoflegends.tag.base.MOLTag;

import java.util.Objects;

public final class MeowOfLegends extends JavaPlugin {
    @Override
    public void onEnable() {
        setupSerialization();
        setupListener();
        setupCommand();
    }

    @Override
    public void onDisable() {
        this.saveConfig();
    }

    public void setupSerialization() {
        //Entity class
        ConfigurationSerialization.registerClass(MOLEntity.class);
        ConfigurationSerialization.registerClass(Megumin.class);

        //Component class
        ConfigurationSerialization.registerClass(MOLComponent.class);
        ConfigurationSerialization.registerClass(AbilityComponent.class);
        ConfigurationSerialization.registerClass(TimerComponent.class);
        ConfigurationSerialization.registerClass(ManaComponent.class);
        ConfigurationSerialization.registerClass(HealthComponent.class);
        ConfigurationSerialization.registerClass(BakuretsuMahou.class);
        ConfigurationSerialization.registerClass(BouncingFireball.class);
        ConfigurationSerialization.registerClass(UrchinStrike.class);
        ConfigurationSerialization.registerClass(SeaStoneTrident.class);

        ConfigurationSerialization.registerClass(SeaStoneTridentTag.class);

        //Tag class
        ConfigurationSerialization.registerClass(MOLTag.class);
    }

    public void setupCommand() {
        addCommand("test", new TestCommand());
        addCommand("debug", new DebugCommand());
        addCommand("manacomponent", new ManaComponentCommand());
        addCommand("healthcomponent", new HealthComponentCommand());
        addCommand("top", new TopCommand());
    }
    public void addCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = Objects.requireNonNull(this.getCommand(commandName));
        command.setExecutor(executor);
    }

    public void setupListener() {
        getServer().getPluginManager().registerEvents(new MOLPlayerLoginHandler(), this);
        getServer().getPluginManager().registerEvents(new LoginMessageHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathMessageHandler(), this);
    }
}
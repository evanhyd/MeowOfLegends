package unboxthecat.meowoflegends;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import unboxthecat.meowoflegends.command.*;
import unboxthecat.meowoflegends.component.fizz.ChumTheWater;
import unboxthecat.meowoflegends.component.fizz.SeaStoneTrident;
import unboxthecat.meowoflegends.component.megumin.BakuretsuMahou;
import unboxthecat.meowoflegends.component.megumin.BouncingFireball;
import unboxthecat.meowoflegends.component.fizz.UrchinStrike;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.MOLComponent;
import unboxthecat.meowoflegends.component.generic.HealthComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.component.generic.StatsBoardComponent;
import unboxthecat.meowoflegends.entity.fizz.Fizz;
import unboxthecat.meowoflegends.entity.megumin.Megumin;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.handler.LoginMessageHandler;
import unboxthecat.meowoflegends.handler.MOLPlayerLoginHandler;
import unboxthecat.meowoflegends.handler.PlayerDeathMessageHandler;
import unboxthecat.meowoflegends.tag.fizz.IsUsingUrchinStrike;
import unboxthecat.meowoflegends.tag.fizz.SeaStoneTridentTag;
import unboxthecat.meowoflegends.tag.generic.IsDashing;
import unboxthecat.meowoflegends.tag.generic.MOLTag;
import unboxthecat.meowoflegends.utility.Timer;

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
        ConfigurationSerialization.registerClass(Fizz.class);

        //Component class
        ConfigurationSerialization.registerClass(MOLComponent.class);
        ConfigurationSerialization.registerClass(ManaComponent.class);
        ConfigurationSerialization.registerClass(HealthComponent.class);
        ConfigurationSerialization.registerClass(AbilityComponent.class);
        ConfigurationSerialization.registerClass(StatsBoardComponent.class);
        ConfigurationSerialization.registerClass(BakuretsuMahou.class);
        ConfigurationSerialization.registerClass(BouncingFireball.class);
        ConfigurationSerialization.registerClass(UrchinStrike.class);
        ConfigurationSerialization.registerClass(SeaStoneTrident.class);
        ConfigurationSerialization.registerClass(ChumTheWater.class);

        //Tag class
        ConfigurationSerialization.registerClass(MOLTag.class);
        ConfigurationSerialization.registerClass(IsDashing.class);
        ConfigurationSerialization.registerClass(IsUsingUrchinStrike.class);
        ConfigurationSerialization.registerClass(SeaStoneTridentTag.class);

        //Utility class
        ConfigurationSerialization.registerClass(Timer.class);
    }

    public void setupCommand() {
        Objects.requireNonNull(this.getCommand("test")).setExecutor(new TestCommand());
        Objects.requireNonNull(this.getCommand("debug")).setExecutor(new DebugCommand());
        Objects.requireNonNull(this.getCommand("manacomponent")).setExecutor(new ManaComponentCommand());
        Objects.requireNonNull(this.getCommand("healthcomponent")).setExecutor(new HealthComponentCommand());
        Objects.requireNonNull(this.getCommand("top")).setExecutor(new TopCommand());
    }

    public void setupListener() {
        getServer().getPluginManager().registerEvents(new MOLPlayerLoginHandler(), this);
        getServer().getPluginManager().registerEvents(new LoginMessageHandler(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathMessageHandler(), this);
        getServer().getPluginManager().registerEvents(new TestCommand(), this);
    }
}
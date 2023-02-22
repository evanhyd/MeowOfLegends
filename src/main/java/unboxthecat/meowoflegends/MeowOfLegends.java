package unboxthecat.meowoflegends;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import unboxthecat.meowoflegends.command.*;
import unboxthecat.meowoflegends.component.ability.fizz.ChumTheWater;
import unboxthecat.meowoflegends.component.ability.fizz.SeaStoneTrident;
import unboxthecat.meowoflegends.component.ability.megumin.BakuretsuMahou;
import unboxthecat.meowoflegends.component.ability.megumin.BouncingFireball;
import unboxthecat.meowoflegends.component.ability.fizz.UrchinStrike;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.base.MOLComponent;
import unboxthecat.meowoflegends.component.generic.HealthComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.component.generic.StatsBoardComponent;
import unboxthecat.meowoflegends.entity.character.Megumin;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.handler.LoginMessageHandler;
import unboxthecat.meowoflegends.handler.MOLPlayerLoginHandler;
import unboxthecat.meowoflegends.handler.PlayerDeathMessageHandler;
import unboxthecat.meowoflegends.tag.ability.fizz.SeaStoneTridentTag;
import unboxthecat.meowoflegends.tag.base.MOLTag;
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

        //Component class
        ConfigurationSerialization.registerClass(MOLComponent.class);
        ConfigurationSerialization.registerClass(AbilityComponent.class);
        ConfigurationSerialization.registerClass(BakuretsuMahou.class);
        ConfigurationSerialization.registerClass(BouncingFireball.class);
        ConfigurationSerialization.registerClass(UrchinStrike.class);
        ConfigurationSerialization.registerClass(SeaStoneTrident.class);
        ConfigurationSerialization.registerClass(ChumTheWater.class);
        ConfigurationSerialization.registerClass(ManaComponent.class);
        ConfigurationSerialization.registerClass(HealthComponent.class);
        ConfigurationSerialization.registerClass(StatsBoardComponent.class);

        //Tag class
        ConfigurationSerialization.registerClass(MOLTag.class);
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
    }
}
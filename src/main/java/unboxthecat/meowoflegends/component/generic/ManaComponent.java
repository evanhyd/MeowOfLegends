package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.component.base.GrowableValueComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;

public class ManaComponent extends GrowableValueComponent implements Listener {
    public class ManaRegenerationTask implements Runnable {
        @Override
        public void run() {
            ManaComponent.this.updateCurrentValue();
            ManaComponent.this.manaBar.setProgress(Math.min(1.0, Math.max(0.0, ManaComponent.this.value / ManaComponent.this.maxValue)));
            ManaComponent.this.manaBar.setTitle(String.format("Mana %.1f (%.1f) / %.1f", ManaComponent.this.value, ManaComponent.this.valueRegeneration, ManaComponent.this.maxValue));
        }
    }

    private MOLEntity owner;
    private BossBar manaBar;
    private BukkitTask manaRegenerationTask;

    public ManaComponent(double baseMaxMana, double maxManaGrowRate, double baseManaRegeneration, double manaRegenerationGrowRate) {
        super(baseMaxMana, maxManaGrowRate, baseManaRegeneration, manaRegenerationGrowRate);
    }

    public ManaComponent(Map<String, Object> data) {
        super(data);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public void onAttach(MOLEntity owner, Object... objects) {
        this.owner = owner;
        if (owner.getEntity() instanceof Player player) {
            this.manaBar = Bukkit.getServer().createBossBar("", BarColor.BLUE, BarStyle.SOLID);
            this.manaBar.addPlayer(player);
        }
        this.manaRegenerationTask = Bukkit.getScheduler().runTaskTimer(GameState.getPlugin(), new ManaRegenerationTask(), 0, GameState.secondToTick(1.0));
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
        updateBaseValue();
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        HandlerList.unregisterAll(this);
        if (this.manaRegenerationTask != null) {
            this.manaRegenerationTask.cancel();
            this.manaRegenerationTask = null;
        }
        if (owner.getEntity() instanceof Player player) {
            this.manaBar.removeAll();
        }
        this.owner = null;
    }

    @Override
    protected void updateBaseValue() {
        int level = ((this.owner.getEntity() instanceof Player player) ? player.getLevel() : 0);
        this.maxValue = Math.max(0.0, this.baseMaxValue + this.maxValueGrowRate * level);
        this.valueRegeneration = this.baseValueRegeneration + this.valueRegenerationGrowRate * level;
        this.value = Math.min(this.maxValue, Math.max(0.0, this.value));
    }

    @Override
    protected void updateCurrentValue() {
        this.value = Math.min(this.maxValue, Math.max(0.0, this.value + this.valueRegeneration));
    }

    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        if (event.getPlayer() == owner.getEntity()) {
            updateBaseValue();
        }
    }

    public void consumeMana(double manaCost) {
        this.value = Math.min(this.maxValue, Math.max(0.0, this.value - manaCost));
    }

    /**
     * Getter and Setter
     */
    public double getBaseMaxMana() {
        return this.baseMaxValue;
    }

    public double getMaxManaGrowRate() {
        return this.maxValueGrowRate;
    }

    public double getBaseManaRegeneration() {
        return this.baseValueRegeneration;
    }

    public double getManaRegenerationGrowRate() {
        return this.valueRegenerationGrowRate;
    }

    public double getMaxMana() {
        return this.maxValue;
    }

    public double getManaRegeneration() {
        return this.valueRegeneration;
    }

    public double getMana() {
        return this.value;
    }

    public void setBaseMaxMana(double baseMaxMana) {
        this.baseMaxValue = Math.max(0.0, baseMaxMana);
        updateBaseValue();
    }

    public void setMaxManaGrowRate(double maxManaGrowRate) {
        this.maxValueGrowRate = maxManaGrowRate;
        updateBaseValue();
    }

    public void setBaseManaRegeneration(double baseManaRegeneration) {
        this.baseValueRegeneration = baseManaRegeneration;
        updateBaseValue();
    }

    public void setManaRegenerationGrowRate(double manaRegenerationGrowRate) {
        this.valueRegenerationGrowRate = manaRegenerationGrowRate;
        updateBaseValue();
    }

    public void setMana(double mana) {
        this.value = Math.min(this.maxValue, Math.max(0.0, mana));
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

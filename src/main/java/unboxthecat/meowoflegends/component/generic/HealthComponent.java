package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
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
import java.util.Objects;

public class HealthComponent extends GrowableValueComponent implements Listener {
    public class HealthRegenerationTask implements Runnable {
        @Override
        public void run() {
            HealthComponent.this.updateCurrentValue();
        }
    }
    private MOLEntity owner;
    private AttributeInstance maxHealthInstance;
    private BukkitTask healthRegenerationTask;

    public HealthComponent(double baseMaxHealth, double maxHealthGrowRate, double baseHealthRegeneration, double healthRegenerationGrowRate) {
        super(baseMaxHealth, maxHealthGrowRate, baseHealthRegeneration, healthRegenerationGrowRate);
    }

    public HealthComponent(Map<String, Object> data) {
        super(data);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public void onAttach(MOLEntity owner, Object... objects) {
        this.owner = owner;
        this.maxHealthInstance = Objects.requireNonNull(((LivingEntity) owner.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH));
        this.healthRegenerationTask = Bukkit.getScheduler().runTaskTimer(GameState.getPlugin(), new HealthRegenerationTask(), 0, GameState.secondToTick(1.0));
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
        updateBaseValue();
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        HandlerList.unregisterAll(this);
        if (this.healthRegenerationTask != null) {
            this.healthRegenerationTask.cancel();
            this.healthRegenerationTask = null;
        }
        this.maxHealthInstance = null;
        this.owner = null;
    }

    @Override
    protected void updateBaseValue() {
        int level = (owner.getEntity() instanceof Player player) ? player.getLevel() : 0;
        this.maxValue = this.baseMaxValue + this.maxValueGrowRate * level;
        this.valueRegeneration = this.baseValueRegeneration + this.valueRegenerationGrowRate * level;
        maxHealthInstance.setBaseValue(this.maxValue);
    }

    @Override
    protected void updateCurrentValue() {
        if (owner.getEntity() instanceof LivingEntity livingEntity) {
            if (!livingEntity.isDead()) {
                double changeInHealth = livingEntity.getHealth() + valueRegeneration;
                changeInHealth = Math.min(maxHealthInstance.getValue(), Math.max(0.0, changeInHealth));
                livingEntity.setHealth(changeInHealth);
            }
        }
    }

    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        if (event.getPlayer() == owner.getEntity()) {
            updateBaseValue();
        }
    }


    /**
     * Getter and Setter
     */
    public double getBaseMaxHealth() {
        return this.baseMaxValue;
    }

    public double getMaxHealthGrowRate() {
        return this.maxValueGrowRate;
    }

    public double getBaseHealthRegeneration() {
        return this.baseValueRegeneration;
    }

    public double getHealthRegenerationGrowRate() {
        return this.valueRegenerationGrowRate;
    }

    public double getMaxHealth() {
        return this.maxValue;
    }

    public double getHealthRegeneration() {
        return this.valueRegeneration;
    }

    public double getHealth() {
        return (owner.getEntity() instanceof Player player) ? player.getHealth() : -1.0;
    }

    public void setBaseMaxHealth(double baseMaxHealth) {
        this.baseMaxValue = Math.max(0.0, baseMaxHealth);
        updateBaseValue();
    }

    public void setMaxHealthGrowRate(double maxHealthGrowRate) {
        this.maxValueGrowRate = maxHealthGrowRate;
        updateBaseValue();
    }

    public void setBaseHealthRegeneration(double baseHealthRegeneration) {
        this.baseValueRegeneration = baseHealthRegeneration;
        updateBaseValue();
    }

    public void setHealthRegenerationGrowRate(double healthRegenerationGrowRate) {
        this.valueRegenerationGrowRate = healthRegenerationGrowRate;
        updateBaseValue();
    }

    public void setHealth(double health) {
        if (owner.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.setHealth(Math.min(this.maxValue, Math.max(0.0, health)));
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                "Health Regeneration Task ID: " + this.healthRegenerationTask.getTaskId() + "\n";
    }
}
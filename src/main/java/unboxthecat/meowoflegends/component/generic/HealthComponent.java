package unboxthecat.meowoflegends.component.generic;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.base.GrowableValueComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;

public class HealthComponent extends GrowableValueComponent {
    private MOLEntity owner;
    private AttributeInstance maxHealthInstance;

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
    public void onAttach(MOLEntity owner) {
        this.owner = owner;
        this.maxHealthInstance = ((LivingEntity)owner.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert this.maxHealthInstance != null;
    }

    @Override
    public void onRemove(MOLEntity owner) {
        this.maxHealthInstance = null;
        this.owner = null;
    }
    @Override
    protected void updateBaseValue() {

    }

    @Override
    protected void updateCurrentValue() {

    }
}

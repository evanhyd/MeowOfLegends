package unboxthecat.meowoflegends.component.base;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

public abstract class GrowableValueComponent implements MOLComponent {
    protected double baseMaxValue;
    protected double maxValueGrowRate;
    protected double baseValueRegeneration;
    protected double valueRegenerationGrowRate;
    protected double maxValue;
    protected double valueRegeneration;
    protected double value;

    protected GrowableValueComponent(double baseMaxValue, double maxValueGrowRate, double baseValueRegeneration, double valueRegenerationGrowRate) {
        this.baseMaxValue = baseMaxValue;
        this.maxValueGrowRate = maxValueGrowRate;
        this.baseValueRegeneration = baseValueRegeneration;
        this.valueRegenerationGrowRate = valueRegenerationGrowRate;
        this.value = 0.0;
    }

    protected GrowableValueComponent(Map<String, Object> data) {
        this.baseMaxValue = (double) data.get("baseMaxValue");
        this.maxValueGrowRate = (double) data.get("maxValueGrowRate");
        this.baseValueRegeneration = (double) data.get("baseValueRegeneration");
        this.valueRegenerationGrowRate = (double) data.get("valueRegenerationGrowRate");
        this.value = (double) data.get("currentValue");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("baseMaxValue", baseMaxValue);
        data.put("maxValueGrowRate", maxValueGrowRate);
        data.put("baseValueRegeneration", baseValueRegeneration);
        data.put("valueRegenerationGrowRate", valueRegenerationGrowRate);
        data.put("currentValue", value);
        return data;
    }

    abstract protected void updateBaseValue();
    abstract protected void updateCurrentValue();

    @Override
    public String toString() {
        return String.format(
        "Base Max Value: %.1f\n" +
        "Max Value Grow Rate: %.1f\n" +
        "Base Value Regeneration: %.1f\n" +
        "Value Regeneration Grow Rate: %.1f\n" +
        "Max Value: %.1f\n" +
        "Value Regeneration Rate: %.1f\n" +
        "Current Value: %.1f",
        this.baseMaxValue, this.maxValueGrowRate, this.baseValueRegeneration, this.valueRegenerationGrowRate, this.maxValue, this.valueRegeneration, this.value);
    }
}

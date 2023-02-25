package unboxthecat.meowoflegends.component.generic;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

public abstract class GrowableValueComponent implements MOLComponent {
    protected double baseMaxValue;
    protected double maxValueGrowRate;
    protected double baseRegeneration;
    protected double regenerationGrowRate;
    protected double maxValue;
    protected double regeneration;
    protected double value;

    protected GrowableValueComponent(double baseMaxValue, double maxValueGrowRate, double baseRegeneration, double valueRegenerationGrowRate) {
        this.baseMaxValue = baseMaxValue;
        this.maxValueGrowRate = maxValueGrowRate;
        this.baseRegeneration = baseRegeneration;
        this.regenerationGrowRate = valueRegenerationGrowRate;
        this.value = 0.0;
    }

    protected GrowableValueComponent(Map<String, Object> data) {
        this.baseMaxValue = (double) data.get("baseMaxValue");
        this.maxValueGrowRate = (double) data.get("maxValueGrowRate");
        this.baseRegeneration = (double) data.get("baseRegeneration");
        this.regenerationGrowRate = (double) data.get("regenerationGrowRate");
        this.value = (double) data.get("value");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("baseMaxValue", baseMaxValue);
        data.put("maxValueGrowRate", maxValueGrowRate);
        data.put("baseRegeneration", baseRegeneration);
        data.put("regenerationGrowRate", regenerationGrowRate);
        data.put("value", value);
        return data;
    }

    abstract protected void updateBaseValue();
    abstract protected void updateCurrentValue();

    @Override
    public String toString() {
        return String.format(
          "Max Value: %.1f = %.1f + %.1fX\n" +
          "Regeneration: %.1f = %.1f + %.1fX\n" +
          "Current Value: %.1f",
          this.maxValue, this.baseMaxValue, this.maxValueGrowRate,
          this.regeneration, this.baseRegeneration, this.regenerationGrowRate,
          this.value);
    }
}

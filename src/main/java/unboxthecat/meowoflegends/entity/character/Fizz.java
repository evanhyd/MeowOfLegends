package unboxthecat.meowoflegends.entity.character;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.ability.fizz.ChumTheWater;
import unboxthecat.meowoflegends.component.ability.fizz.SeaStoneTrident;
import unboxthecat.meowoflegends.component.ability.fizz.UrchinStrike;
import unboxthecat.meowoflegends.component.generic.HealthComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.Objects;

public class Fizz extends MOLEntity {
    public Fizz(Entity entity) {
        super(entity);
        attachComponent(new HealthComponent(BASE_MAX_HEALTH, MAX_HEALTH_GROW_RATE, BASE_HEALTH_REGENERATION, HEALTH_REGENERATION_GROW_RATE));
        attachComponent(new ManaComponent(BASE_MAX_MANA, MAX_MANA_GROW_RATE, BASE_MANA_REGENERATION, MANA_REGENERATION_GROW_RATE));
        attachComponent(new UrchinStrike(0));
        attachComponent(new SeaStoneTrident(1));
        attachComponent(new ChumTheWater(2));
    }

    public Fizz(Map<String, Object> data) {
        super(data);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public void activate(Entity entity) {
        this.entity = entity;
        Objects.requireNonNull(getComponent(HealthComponent.class)).onAttach(this);
        Objects.requireNonNull(getComponent(ManaComponent.class)).onAttach(this);
        Objects.requireNonNull(getComponent(UrchinStrike.class)).onAttach(this);
        Objects.requireNonNull(getComponent(SeaStoneTrident.class)).onAttach(this);
        Objects.requireNonNull(getComponent(ChumTheWater.class)).onAttach(this);
    }

    @Override
    public void deactivate() {
        Objects.requireNonNull(getComponent(ChumTheWater.class)).onRemove(this);
        Objects.requireNonNull(getComponent(SeaStoneTrident.class)).onRemove(this);
        Objects.requireNonNull(getComponent(UrchinStrike.class)).onRemove(this);
        Objects.requireNonNull(getComponent(ManaComponent.class)).onRemove(this);
        Objects.requireNonNull(getComponent(HealthComponent.class)).onRemove(this);
    }

    @Override
    public void destroy() {
        removeComponent(ChumTheWater.class);
        removeComponent(SeaStoneTrident.class);
        removeComponent(UrchinStrike.class);
        removeComponent(ManaComponent.class);
        removeComponent(HealthComponent.class);
    }

    //Fizz Stats
    private static final double BASE_MAX_HEALTH = 10.0;
    private static final double MAX_HEALTH_GROW_RATE = 10.0/30.0;
    private static final double BASE_HEALTH_REGENERATION = 0.0;
    private static final double HEALTH_REGENERATION_GROW_RATE = 0.25 / 30.0;

    private static final double BASE_MAX_MANA = 100.0;
    private static final double MAX_MANA_GROW_RATE = 200.0/30.0;
    private static final double BASE_MANA_REGENERATION = 2.5;
    private static final double MANA_REGENERATION_GROW_RATE = 2.5 / 30.0;
}

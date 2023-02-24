package unboxthecat.meowoflegends.entity.character;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.ability.megumin.BakuretsuMahou;
import unboxthecat.meowoflegends.component.ability.megumin.BouncingFireball;
import unboxthecat.meowoflegends.component.generic.HealthComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.component.generic.StatsBoardComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.Objects;

public class Megumin extends MOLEntity {

    public Megumin(Entity entity) {
        super(entity);
        attachComponent(new StatsBoardComponent());
        attachComponent(new HealthComponent(BASE_MAX_HEALTH, MAX_HEALTH_GROW_RATE, BASE_HEALTH_REGENERATION, HEALTH_REGENERATION_GROW_RATE));
        attachComponent(new ManaComponent(BASE_MAX_MANA, MAX_MANA_GROW_RATE, BASE_MANA_REGENERATION, MANA_REGENERATION_GROW_RATE));
        attachComponent(new BouncingFireball(0));
        attachComponent(new BakuretsuMahou(1));
    }

    public Megumin(Map<String, Object> data) {
        super(data);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public void activate(Entity entity) {
        this.entity = entity;
        Objects.requireNonNull(getComponent(StatsBoardComponent.class)).onAttach(this);
        Objects.requireNonNull(getComponent(HealthComponent.class)).onAttach(this);
        Objects.requireNonNull(getComponent(ManaComponent.class)).onAttach(this);
        Objects.requireNonNull(getComponent(BouncingFireball.class)).onAttach(this);
        Objects.requireNonNull(getComponent(BakuretsuMahou.class)).onAttach(this);
    }

    @Override
    public void deactivate() {
        Objects.requireNonNull(getComponent(BakuretsuMahou.class)).onRemove(this);
        Objects.requireNonNull(getComponent(BouncingFireball.class)).onRemove(this);
        Objects.requireNonNull(getComponent(ManaComponent.class)).onRemove(this);
        Objects.requireNonNull(getComponent(HealthComponent.class)).onRemove(this);
        Objects.requireNonNull(getComponent(StatsBoardComponent.class)).onRemove(this);
    }

    @Override
    public void destroy() {
        removeComponent(BakuretsuMahou.class);
        removeComponent(BouncingFireball.class);
        removeComponent(ManaComponent.class);
        removeComponent(HealthComponent.class);
        removeComponent(StatsBoardComponent.class);
    }

    //Megumin Stats
    private static final double BASE_MAX_HEALTH = 10.0;
    private static final double MAX_HEALTH_GROW_RATE = 10.0/30.0;
    private static final double BASE_HEALTH_REGENERATION = 0.0;
    private static final double HEALTH_REGENERATION_GROW_RATE = 0.25 / 30.0;

    private static final double BASE_MAX_MANA = 100.0;
    private static final double MAX_MANA_GROW_RATE = 200.0/30.0;
    private static final double BASE_MANA_REGENERATION = 2.5;
    private static final double MANA_REGENERATION_GROW_RATE = 2.5 / 30.0;
}

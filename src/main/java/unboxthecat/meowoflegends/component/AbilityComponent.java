package unboxthecat.meowoflegends.component;

import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.entity.MOLEntity;

import java.util.Map;

public class AbilityComponent implements MOLComponent {
    public static class Ability {

    }

    public AbilityComponent(Ability... abilities) {
        //todo
    }

    @Override
    public void onAttach(MOLEntity owner) {

    }

    @Override
    public void onRemove(MOLEntity owner) {

    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}

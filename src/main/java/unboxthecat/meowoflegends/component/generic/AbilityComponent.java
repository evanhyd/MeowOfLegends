package unboxthecat.meowoflegends.component.generic;

import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Arrays;
import java.util.Set;

public abstract class AbilityComponent implements MOLComponent {
    private final boolean isActiveAbility;
    private int abilitySlot;

    protected AbilityComponent(boolean isActiveAbility) {
        this.isActiveAbility = isActiveAbility;
    }

    /**
     * Assign the first available item slot to the ability.
     * This function should be called inside onAttach().
     * @param molEntity the owner
     */
    protected void setUpAbilitySlot(MOLEntity molEntity) {

        if (!isActiveAbility) {
            return;
        }

        boolean[] availableSlot = new boolean[9];
        Arrays.fill(availableSlot, true);

        for (MOLComponent component : molEntity.getComponents()) {
            if (component instanceof AbilityComponent) {
                availableSlot[((AbilityComponent) component).abilitySlot] = false;
            }
        }

        for (int i = 0; i < availableSlot.length; ++i) {
            if (availableSlot[i]) {
                abilitySlot = i;
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "Active Ability: " + isActiveAbility + "\n" +
                "Ability Slot: " + abilitySlot + "\n";
    }
}

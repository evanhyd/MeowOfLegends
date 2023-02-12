package unboxthecat.meowoflegends.component.base;

import org.bukkit.entity.Player;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.lang.reflect.Array;
import java.util.Arrays;

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

        molEntity.getComponents().stream()
                .filter(component -> component instanceof AbilityComponent && component != this)
                .forEach(component -> availableSlot[((AbilityComponent) component).abilitySlot] = false);

        for (int i = 0; i < availableSlot.length; ++i) {
            if (availableSlot[i]) {
                abilitySlot = i;
                break;
            }
        }
    }

    protected boolean isUsingAbilitySlot(Player player) {
        return player.getInventory().getHeldItemSlot() == abilitySlot;
    }

    @Override
    public String toString() {
        return "Active Ability: " + isActiveAbility + "\n" +
               "Ability Slot: " + abilitySlot + "\n";
    }
}

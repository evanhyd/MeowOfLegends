package unboxthecat.meowoflegends.component.generic;

import org.bukkit.event.Listener;
import unboxthecat.meowoflegends.component.MOLComponent;

public interface AbilityComponent extends MOLComponent {

    /**
     * Describe the behavior of the abilities.
     * Ex. Fizz's Urchin Strike dash through target and deal damage.
     */
    void activate();
}

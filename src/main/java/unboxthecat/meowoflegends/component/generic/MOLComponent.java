package unboxthecat.meowoflegends.component.generic;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

public interface MOLComponent extends ConfigurationSerializable {

    /**
     * Called when attaching to the MOLEntity.
     * @param owner the MOLEntity.
     */
    void onAttach(MOLEntity owner);

    /**
     * Called when removing from the MOLEntity.
     * @param owner the MOLEntity.
     */
    void onRemove(MOLEntity owner);
}

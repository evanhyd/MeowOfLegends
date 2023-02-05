package unboxthecat.meowoflegends.component;

import unboxthecat.meowoflegends.entity.MOLEntity;

import java.util.Map;

public interface MOLComponent {

    /**
     * Serialize the object into a map.
     * @return serialized data.
     */
    Map<String, Object> serialize();

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

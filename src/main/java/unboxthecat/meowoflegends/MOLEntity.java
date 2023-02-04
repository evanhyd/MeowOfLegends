package unboxthecat.meowoflegends;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MOLEntity {
    private Entity entity;
    private final Map<Class<?>, MOLComponent> components;
    private final Map<Class<?>, MOLTag> tags;

    private MOLEntity() {
        components = new HashMap<>();
        tags = new HashMap<>();
    }

    /**
     * Construct a MOLEntity given by an Entity UUID.
     * @param uuid Entity UUID.
     */
    MOLEntity(UUID uuid) {
        this();
        entity = Bukkit.getEntity(uuid);
    }

    /**
     * Construct a MOLEntity given by an Entity instance.
     * @param entity Entity instance.
     */
    MOLEntity(Entity entity) {
        this();
        this.entity = entity;
    }

    /**
     * Attach a MOLComponent to the MOLEntity.
     * Replace the old MOLComponent if already attached.
     * @param component MOLComponent to attach.
     */
    public void attachComponent(MOLComponent component) {
        removeComponent(component.getClass());
        components.put(component.getClass(), component);
        component.onAttach(this);
    }

    /**
     * Remove a MOLComponent by class from the MOLEntity.
     * Nothing happens if such MOLComponent is not already attached.
     * @param componentClass class of MOLComponent to remove.
     */
    public void removeComponent(Class<?> componentClass) {
        final MOLComponent component = components.get(componentClass);
        if (component != null) {
            component.onRemove(this);
        }
        components.remove(componentClass);
    }

    /**
     * Check if the MOLEntity has a MOLComponent by class.
     * @param componentClass class of MOLComponent to check.
     * @return True if the MOLEntity has a MOLComponent with the same class.
     */
    public boolean hasComponent(Class<?> componentClass) {
        return components.containsKey(componentClass);
    }

    /**
     * Get a MOLComponent by class from the MOLEntity.
     * @param componentClass class of MOLComponent to get.
     * @return MOLComponent if exists, null otherwise.
     */
    public MOLComponent getComponent(Class<?> componentClass) {
        return components.get(componentClass);
    }

    /**
     * Attach a MOLTag to the MOLEntity.
     * @param tag MOLTag to attach.
     */
    public void attachTag(MOLTag tag) {
        tags.put(tag.getClass(), tag);
    }

    /**
     * Remove a MOLTag from the MOLEntity.
     * Nothing happens if such MOLTag is not already attached.
     * @param tagClass class of MOLTag to remove.
     */
    public void removeTag(Class<?> tagClass) {
        tags.remove(tagClass);
    }

    /**
     * Check if the MOLEntity has a MOLTag by class.
     * @param tagClass class of MOLTag to check.
     * @return True if the MOLEntity has a MOLTag with the same class.
     */
    public boolean hasTag(Class<?> tagClass) {
        return tags.containsKey(tagClass);
    }

    /**
     * Get a MOLTag by class from the MOLEntity.
     * @param tagClass class of MOLTag to get.
     * @return MOLTag if exists, null otherwise.
     */
    public MOLTag getTag(Class<?> tagClass) {
        return tags.get(tagClass);
    }
}

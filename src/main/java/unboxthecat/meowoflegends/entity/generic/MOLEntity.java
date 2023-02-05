package unboxthecat.meowoflegends.entity.generic;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.generic.MOLComponent;
import unboxthecat.meowoflegends.tag.MOLTag;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class MOLEntity implements ConfigurationSerializable {
    private final Entity entity;
    private Map<String, MOLComponent> components;
    private Map<String, MOLTag> tags;

    /**
     * Construct a MOLEntity given by an Entity instance.
     * @param entity Entity instance.
     */
    public MOLEntity(Entity entity) {
        this.entity = entity;
        components = new TreeMap<>();
        tags = new TreeMap<>();
    }

    /**
     * Construct a MOLEntity given by the serialized data.
     * @param data serialized data.
     */
    public MOLEntity(Map<String, Object> data) {
        this(Bukkit.getPlayer(UUID.fromString((String) data.get("UUID"))));
        components = (Map<String, MOLComponent>) data.get("components");
        tags = (Map<String, MOLTag>) data.get("tags");
        components.forEach((name, component) -> component.onAttach(this));
    }

    /**
     * Configurable Serialization is buggy as fk, I may as well manually create one.
     * @return serialized data.
     */
    @Override
    public @NotNull Map<String,Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("UUID", entity.getUniqueId().toString());
        data.put("components", components);
        data.put("tags", tags);
        return data;
    }

    /**
     * Get Entity, can be null.
     * @return Entity instance.
     */
    public Entity getEntity() {
        return this.entity;
    }

    /**
     * Get a MOLComponent by class from the MOLEntity.
     * @param componentClass class of MOLComponent to get.
     * @return MOLComponent if exists, null otherwise.
     */
    public MOLComponent getComponent(Class<?> componentClass) {
        return components.get(componentClass.getName());
    }

    /**
     * Get a MOLTag by class from the MOLEntity.
     * @param tagClass class of MOLTag to get.
     * @return MOLTag if exists, null otherwise.
     */
    public MOLTag getTag(Class<?> tagClass) {
        return tags.get(tagClass.getName());
    }

    /**
     * Check if the MOLEntity has a MOLComponent by class.
     * @param componentClass class of MOLComponent to check.
     * @return True if the MOLEntity has a MOLComponent with the same class.
     */
    public boolean hasComponent(Class<?> componentClass) {
        return components.containsKey(componentClass.getName());
    }

    /**
     * Check if the MOLEntity has a MOLTag by class.
     * @param tagClass class of MOLTag to check.
     * @return True if the MOLEntity has a MOLTag with the same class.
     */
    public boolean hasTag(Class<?> tagClass) {
        return tags.containsKey(tagClass.getName());
    }

    /**
     * Attach a MOLComponent to the MOLEntity.
     * Replace the old MOLComponent if already attached.
     * @param component MOLComponent to attach.
     */
    public void attachComponent(MOLComponent component) {
        removeComponent(component.getClass());
        components.put(component.getClass().getName(), component);
        component.onAttach(this);
    }

    /**
     * Attach a MOLTag to the MOLEntity.
     * @param tag MOLTag to attach.
     */
    public void attachTag(MOLTag tag) {
        tags.put(tag.getClass().getName(), tag);
    }

    /**
     * Remove a MOLComponent by class from the MOLEntity.
     * Nothing happens if such MOLComponent is not already attached.
     * @param componentClass class of MOLComponent to remove.
     */
    public void removeComponent(Class<?> componentClass) {
        final MOLComponent component = components.get(componentClass.getName());
        if (component != null) {
            component.onRemove(this);
        }
        components.remove(componentClass.getName());
    }

    /**
     * Remove a MOLTag from the MOLEntity.
     * Nothing happens if such MOLTag is not already attached.
     * @param tagClass class of MOLTag to remove.
     */
    public void removeTag(Class<?> tagClass) {
        tags.remove(tagClass.getName());
    }

    public void destroy() {
        components.forEach((name, component) -> component.onRemove(this));
    }
}

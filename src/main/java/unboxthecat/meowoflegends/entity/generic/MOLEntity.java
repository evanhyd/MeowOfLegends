package unboxthecat.meowoflegends.entity.generic;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unboxthecat.meowoflegends.component.base.MOLComponent;
import unboxthecat.meowoflegends.tag.base.MOLTag;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public abstract class MOLEntity implements ConfigurationSerializable {
    protected Entity entity;
    protected final Map<String, MOLComponent> components;
    protected final Map<String, MOLTag> tags;

    /**
     * Construct a MOLEntity given by an Entity instance.
     * @param entity Entity instance.
     */
    protected MOLEntity(Entity entity) {
        this.entity = entity;
        components = new TreeMap<>();
        tags = new TreeMap<>();
    }

    /**
     * Construct a MOLEntity given by the serialized data.
     * @param data serialized data.
     */
    protected MOLEntity(Map<String, Object> data) {
        components = (Map<String, MOLComponent>) data.get("components");
        tags = (Map<String, MOLTag>) data.get("tags");
    }

    /**
     * Configurable Serialization is buggy as fk, I may as well manually create one.
     * @return serialized data.
     */
    @Override
    public @NotNull Map<String,Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
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
    @Nullable
    public <T extends MOLComponent> T  getComponent(Class<T> componentClass) {
        return componentClass.cast(components.get(componentClass.getName()));
    }

    /**
     * Get a MOLTag by class from the MOLEntity.
     * @param tagClass class of MOLTag to get.
     * @return MOLTag if exists, null otherwise.
     */
    @Nullable
    public <T extends MOLTag> T getTag(Class<T> tagClass) {
        return tagClass.cast(tags.get(tagClass.getName()));
    }

    /**
     * Get all MOLComponents from the MOLEntity.
     * @return components.
     */
    public Collection<MOLComponent> getComponents() {
        return components.values();
    }

    /**
     * Get all MOLTag from the MOLEntity.
     * @return tags.
     */
    public Collection<MOLTag> getTags() {
        return tags.values();
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

    /**
     * Activate all character specific components.
     * Called after loading from a config file.
     * @param entity the owner Entity.
     */
    abstract public void activate(Entity entity);

    /**
     * Deactivate all character specific components.
     * Called before player disconnecting from the server.
     */
    abstract public void deactivate();

    /**
     * Remove character specific components.
     * Called before removing the character.
     */
    abstract public void destroy();

    @Override
    public String toString() {
        String entityInfo = "Name    : " + entity.getName() + "\n" +
                            "UUID    : " + entity.getUniqueId() + "\n" +
                            "Vehicle : " + (entity.getVehicle() == null ? "no vehicle" : entity.getVehicle().toString()) + "\n" +
                            "FireTicks: " + entity.getFireTicks()+ "\n" +
                            "FreezeTicks: " + entity.getFreezeTicks() + "\n";

        StringBuilder componentsInfo = new StringBuilder();
        components.forEach((name, component) -> componentsInfo.append(name).append(" :\n").append(component).append("\n"));

        StringBuilder tagsInfo = new StringBuilder();
        tags.forEach((name, component) -> tagsInfo.append(name).append(" :\n").append(component).append("\n"));

        return  ChatColor.BLUE + "EntityInfo:\n" + ChatColor.LIGHT_PURPLE + entityInfo + "\n" +
                ChatColor.BLUE +  "ComponentsInfo:\n" + ChatColor.LIGHT_PURPLE + componentsInfo + "\n" +
                ChatColor.BLUE + "TagsInfo:\n" + ChatColor.LIGHT_PURPLE + tagsInfo + "\n";
    }
}

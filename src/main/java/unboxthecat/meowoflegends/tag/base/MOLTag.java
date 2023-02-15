package unboxthecat.meowoflegends.tag.base;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface MOLTag extends ConfigurationSerializable {
    @Override
    @NotNull
    Map<String, Object> serialize();
}


package unboxthecat.meowoflegends.tag.generic;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

public class IsDashing implements MOLTag {
  public IsDashing() {}

  public IsDashing(Map<String, Object> data) {}

  @Override
  public @NotNull Map<String, Object> serialize() {
    Map<String, Object> data = new TreeMap<>();
    return data;
  }
}

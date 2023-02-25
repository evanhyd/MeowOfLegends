package unboxthecat.meowoflegends.tag.fizz;

import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.tag.generic.MOLTag;

import java.util.Map;
import java.util.TreeMap;

public class SeaStoneTridentTag implements MOLTag {
    public SeaStoneTridentTag() {}
    public SeaStoneTridentTag(Map<String, Object> data) {}
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        return data;
    }
}

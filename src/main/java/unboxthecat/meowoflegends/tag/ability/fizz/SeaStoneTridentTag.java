package unboxthecat.meowoflegends.tag.ability.fizz;

import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.ability.fizz.SeaStoneTrident;
import unboxthecat.meowoflegends.tag.base.MOLTag;

import java.util.Map;
import java.util.TreeMap;

public class SeaStoneTridentTag implements MOLTag {

    public SeaStoneTridentTag(){}

    public SeaStoneTridentTag(Map<String, Object> map){}
    @Override
    public @NotNull Map<String, Object> serialize() {
        return new TreeMap<>();
    }
}

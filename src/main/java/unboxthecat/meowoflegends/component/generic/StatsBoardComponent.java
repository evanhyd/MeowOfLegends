package unboxthecat.meowoflegends.component.generic;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.base.MOLComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class StatsBoardComponent implements MOLComponent {
    private final Map<String, String> statsEntryMap;
    private Player player;
    private Objective stats;

    public StatsBoardComponent() {
        this.statsEntryMap = new TreeMap<>();
    }

    public StatsBoardComponent(Map<String, Object> data) {
        this.statsEntryMap = new TreeMap<>();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner, Object... objects) {
        this.player = (Player) owner.getEntity();
        render();
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        this.stats.unregister();
        this.stats = null;
        this.player = null;
        this.statsEntryMap.clear();
    }

    public void set(String title, String content) {
        statsEntryMap.put(title, title + ": " + content);
        render();
    }

    public void remove(String title) {
        statsEntryMap.remove(title);
        render();
    }

    public void render() {
        if (stats != null) {
            stats.unregister();
        }
        stats = player.getScoreboard().registerNewObjective("StatsBoard", Criteria.DUMMY, "Stats");
        stats.setDisplaySlot(DisplaySlot.SIDEBAR);
        statsEntryMap.forEach((title, entry) -> stats.getScore(entry).setScore(0));
    }

    @Override
    public String toString() {
        return "";
    }
}

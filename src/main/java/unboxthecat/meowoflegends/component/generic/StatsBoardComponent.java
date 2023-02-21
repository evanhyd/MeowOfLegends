package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.base.MOLComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class StatsBoardComponent implements MOLComponent {
    private MOLEntity owner;
    private final Objective stats;

    StatsBoardComponent() {
        ScoreboardManager manager = Objects.requireNonNull(Bukkit.getScoreboardManager());
        Scoreboard statsBoard = manager.getNewScoreboard();
        this.stats = statsBoard.registerNewObjective("Stats Board", Criteria.DUMMY, "Stats");
        this.stats.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    StatsBoardComponent(Map<String, Object> data) {
        ScoreboardManager manager = Objects.requireNonNull(Bukkit.getScoreboardManager());
        Scoreboard statsBoard = manager.getNewScoreboard();
        this.stats = statsBoard.registerNewObjective("Stats Board", Criteria.DUMMY, "Stats");
        this.stats.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner, Object... objects) {
        this.owner = owner;
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        this.owner = null;
    }
}

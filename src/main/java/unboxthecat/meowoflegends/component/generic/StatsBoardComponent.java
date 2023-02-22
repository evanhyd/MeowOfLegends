package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.base.MOLComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class StatsBoardComponent implements MOLComponent {
    private Objective stats;

    public StatsBoardComponent() {
    }

    public StatsBoardComponent(Map<String, Object> data) {
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner, Object... objects) {
        if (owner.getEntity() instanceof Player player) {
            player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
            this.stats = player.getScoreboard().registerNewObjective(player.getName() + " Stats Board", Criteria.DUMMY, "Stats");
            this.stats.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        if (owner.getEntity() instanceof Player player) {
            player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
        }
    }

    public void setStats(String statsTitle, String statsData) {
        stats.getScore(statsTitle + ": " + statsData).setScore(0);
    }

    @Override
    public String toString() {
        return "";
    }
}

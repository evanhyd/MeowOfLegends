package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class ManaComponent implements MOLComponent {
    private class ManaRegenerationTask extends BukkitRunnable {
        @Override
        public void run() {
            currentMana = Math.min(maxMana, currentMana + manaRegenerationRate);
            manaBar.setProgress(currentMana / maxMana);
            manaBar.setTitle(getManaTitle());
        }
    }

    private double currentMana;
    private double maxMana;
    private double manaRegenerationRate;
    private BossBar manaBar;
    private final ManaRegenerationTask manaRegenerationTask;

    public ManaComponent(double currentMana, double maxMana, double manaRegenerationRate) {
        this.currentMana = currentMana;
        this.maxMana = maxMana;
        this.manaRegenerationRate = manaRegenerationRate;
        this.manaBar = Bukkit.getServer().createBossBar(getManaTitle(), BarColor.BLUE, BarStyle.SOLID);
        this.manaRegenerationTask = new ManaRegenerationTask();
    }

    public ManaComponent(Map<String, Object> data) {
        currentMana = (double) data.get("currentMana");
        maxMana = (double) data.get("maxMana");
        manaRegenerationRate = (double) data.get("manaRegenerationRate");
        manaBar = Bukkit.getServer().createBossBar(getManaTitle(), BarColor.BLUE, BarStyle.SOLID);
        manaRegenerationTask = new ManaRegenerationTask();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("currentMana", currentMana);
        data.put("maxMana", maxMana);
        data.put("manaRegenerationRate", manaRegenerationRate);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner) {
        if (owner.getEntity() instanceof Player) {
            manaBar.addPlayer((Player)owner.getEntity());
            manaBar.setVisible(true);
        }
        manaRegenerationTask.runTaskTimerAsynchronously(GameState.getPlugin(), 0, GameState.secondToTick(1.0));
    }

    @Override
    public void onRemove(MOLEntity owner) {
        manaRegenerationTask.cancel();
        manaBar.removeAll();
    }

    private String getManaTitle() {
        String rate = (currentMana < maxMana ? (manaRegenerationRate >= 0.0 ? "+ " : "- ") + manaRegenerationRate + " ": "");
        return String.format("Mana %.1f %s/ %.1f", currentMana, rate, maxMana);
    }

    public double getCurrentMana() {
        return currentMana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public double getManaRegenerationRate() {
        return manaRegenerationRate;
    }

    public void consumeMana(double manaCost) {
        currentMana = Math.max(0.0, currentMana - manaCost);
    }

    //Overriding toString() method of String Class
    @Override
    public String toString(){
        return "currMana: " + this.currentMana + "\n" +
                "maxMana: " + this.maxMana + "\n" +
                "manaRegenRate: " + this.manaRegenerationRate + "\n" +
                "manaBar(Title): " + this.manaBar.getTitle() + "\n" +
                "manaRegenTask(TaskId): " + this.manaRegenerationTask.getTaskId() + "\n" ;
    }
}

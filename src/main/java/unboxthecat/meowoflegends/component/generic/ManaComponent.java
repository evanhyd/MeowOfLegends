package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class ManaComponent implements MOLComponent {
    private class ManaRegenerationTask implements Runnable {
        @Override
        public void run() {
            currentMana = Math.min(maxMana, Math.max(0.0, currentMana + manaRegenerationRate));
            manaBar.setProgress(currentMana / maxMana);
            manaBar.setTitle(getManaTitle());
        }
    }

    private double currentMana;
    private double maxMana;
    private double manaRegenerationRate;
    private final BossBar manaBar;
    private BukkitTask manaRegenerationTask;

    public ManaComponent(double currentMana, double maxMana, double manaRegenerationRate) {
        this.currentMana = currentMana;
        this.maxMana = maxMana;
        this.manaRegenerationRate = manaRegenerationRate;
        this.manaBar = Bukkit.getServer().createBossBar(getManaTitle(), BarColor.BLUE, BarStyle.SOLID);
    }

    public ManaComponent(Map<String, Object> data) {
        currentMana = (double) data.get("currentMana");
        maxMana = (double) data.get("maxMana");
        manaRegenerationRate = (double) data.get("manaRegenerationRate");
        manaBar = Bukkit.getServer().createBossBar(getManaTitle(), BarColor.BLUE, BarStyle.SOLID);
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

        manaRegenerationTask = Bukkit.getScheduler().runTaskTimer(GameState.getPlugin(), new ManaRegenerationTask(), 0, GameState.secondToTick(1.0));
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

    public void setCurrentMana(double newCurrentMana){ this.currentMana = newCurrentMana; }

    public void setMaxMana(double newMaxMana) { this.maxMana = newMaxMana; }

    public void setManaRegenerationRate(double newManaRegenerationRate) { this.manaRegenerationRate = newManaRegenerationRate; }

    public void consumeMana(double manaCost) {
        currentMana = Math.min(maxMana, Math.max(0.0, currentMana - manaCost));
    }

    @Override
    public String toString(){
        return "currMana: " + this.currentMana + "\n" +
                "maxMana: " + this.maxMana + "\n" +
                "manaRegenRate: " + this.manaRegenerationRate + "\n" +
                "manaBar(Title): " + this.manaBar.getTitle() + "\n" +
                "manaRegenTask(TaskId): " + this.manaRegenerationTask.getTaskId() + "\n" ;
    }
}

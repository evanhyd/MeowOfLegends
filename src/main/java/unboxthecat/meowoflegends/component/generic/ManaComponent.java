package unboxthecat.meowoflegends.component.generic;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.MOLComponent;
import unboxthecat.meowoflegends.entity.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class ManaComponent implements MOLComponent {
    private class ManaRegenerationTask extends BukkitRunnable {
        @Override
        public void run() {
            currentMana = Math.min(maxMana, currentMana + manaRegenerationRate);
            manaBar.setProgress(currentMana / maxMana);
            manaBar.setTitle(getManaTitle());

            System.out.println("running mana");
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
        manaBar = (BossBar) data.get("manaBar");
        manaRegenerationTask = new ManaRegenerationTask();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("currentMana", currentMana);
        data.put("maxMana", maxMana);
        data.put("manaRegenerationRate", manaRegenerationRate);
        data.put("manaBar", manaBar);
        return data;
    }

    private String getManaTitle() {
        String rate = (currentMana != maxMana ? (manaRegenerationRate >= 0.0 ? "+ " : "- ") + manaRegenerationRate + " ": "");
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

    @Override
    public void onAttach(MOLEntity owner) {
        System.out.println("attached");
        if (owner.getEntity() instanceof Player) {
            manaBar.addPlayer((Player)owner.getEntity());
            manaBar.setVisible(true);
            System.out.println("debug, set visible");
        }
        manaRegenerationTask.runTaskTimerAsynchronously(GameState.getPlugin(), 0, GameState.secondToTick(1.0));
    }

    @Override
    public void onRemove(MOLEntity owner) {
        manaRegenerationTask.cancel();
        manaBar.removeAll();
    }
}

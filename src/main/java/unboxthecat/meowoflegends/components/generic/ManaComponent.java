package unboxthecat.meowoflegends.components.generic;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.MOLComponent;
import unboxthecat.meowoflegends.MOLEntity;
import unboxthecat.meowoflegends.MeowOfLegends;

import java.io.Serializable;

public class ManaComponent implements MOLComponent, Listener, Serializable {
    private class ManaRegenerationTask extends BukkitRunnable {
        @Override
        public void run() {
            gainMana(manaRegenerationRate);
        }
    }

    private double currentMana;
    private double maxMana;
    private double manaRegenerationRate;
    private BossBar manaBar;
    private ManaRegenerationTask manaRegenerationTask;

    public ManaComponent() {
        currentMana = 0.0;
        maxMana = 0.0;
        manaRegenerationRate = 0.0;
        manaBar = Bukkit.getServer().createBossBar("Mana", BarColor.BLUE, BarStyle.SOLID);
        manaRegenerationTask = new ManaRegenerationTask();
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

    public void gainMana(double manaGain) {
        currentMana = Math.max(maxMana, currentMana + manaGain);
    }


    @Override
    public void onAttach(MOLEntity owner) {
        if (owner instanceof Player) {
            manaBar.addPlayer((Player)owner);
        }
        manaRegenerationTask.runTaskTimerAsynchronously(GameState.getPlugin(), 0, GameState.secondToTick(1.0));
    }

    @Override
    public void onRemove(MOLEntity owner) {
        manaRegenerationTask.cancel();
        manaBar.removeAll();
    }
}

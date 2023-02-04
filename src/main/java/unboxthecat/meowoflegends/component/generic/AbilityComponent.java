package unboxthecat.meowoflegends.component.generic;

import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.MOLComponent;
import unboxthecat.meowoflegends.entity.MOLEntity;

import java.util.Map;

public class AbilityComponent implements MOLComponent {

    public static class Ability {

        public class CooldownTask extends BukkitRunnable {
            @Override
            public void run() {

            }
        }
        int currentCooldown;
        int maxCooldown;

        public Ability(int maxCooldown) {
            this.currentCooldown = maxCooldown;
            this.maxCooldown = maxCooldown;
        }
    }

    public AbilityComponent(Ability... abilities) {
        //todo
    }

    @Override
    public void onAttach(MOLEntity owner) {

    }

    @Override
    public void onRemove(MOLEntity owner) {

    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}

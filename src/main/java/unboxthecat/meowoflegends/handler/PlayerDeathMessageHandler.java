package unboxthecat.meowoflegends.handler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathMessageHandler implements Listener {
    @EventHandler
    void sendDeathMessage(PlayerDeathEvent event) {
        EntityDamageEvent lastDamageEvent = event.getEntity().getLastDamageCause();
        if (lastDamageEvent == null) {
            return;
        }

        Entity entity = event.getEntity();
        String name = entity.getName();

        //death message
        EntityDamageEvent.DamageCause damageCause = lastDamageEvent.getCause();
        switch (damageCause) {
            case ENTITY_ATTACK -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " got ratio");
            }
            case ENTITY_SWEEP_ATTACK -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " got ratio + L");
            }
            case DROWNING -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " ran out of O2");
            }
            case FALL -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " tried to defy Newton");
            }
            case FALLING_BLOCK -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " got bonked");
            }
            case FLY_INTO_WALL -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " crashed into a pile of junk");
            }
            case LAVA -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " forgot to add lava block to the x-ray");
            }
            case LIGHTNING -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " got smite");
            }
            case SUFFOCATION -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " suffocated in body pillows");
            }
            case THORNS -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " punched a hedgehog");
            }
            case VOID -> {
                event.setDeathMessage(ChatColor.RED + name + ChatColor.RED + " fell into a dark abyss");
            }
        }

        //death coordinates
        Location location = entity.getLocation();
        entity.sendMessage(String.format(ChatColor.BOLD + "Death Location: %d %d %d",
                location.getBlockX(), location.getBlockY(), location.getBlockZ())
        );
    }
}

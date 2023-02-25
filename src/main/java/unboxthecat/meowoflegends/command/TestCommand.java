package unboxthecat.meowoflegends.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("Executed test command");
        if (sender instanceof Player player) {
            player.setPlayerListName("\n");
            player.setGliding(!player.isGliding());
            player.setGravity(!player.hasGravity());
            player.sendMessage("Gliding: " + player.isGliding());
        }
        return true;
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        event.setCancelled(true);
    }
}

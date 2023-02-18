package unboxthecat.meowoflegends.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            int y = player.getWorld().getHighestBlockYAt(player.getLocation()) + 1;
            player.getLocation().setY(y);
            return true;
        }
        return false;
    }
}

package unboxthecat.meowoflegends.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;

public class CommandDebug implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(sender instanceof Player player){
            player.sendMessage(GameState.getPlayers().get(player.getUniqueId()).toString());
            return true;
        }
        return false;
    }

}

package unboxthecat.meowoflegends.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.generic.MOLComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.tag.MOLTag;

import java.util.Map;
import java.util.UUID;

import static unboxthecat.meowoflegends.GameState.getPlayers;

/**
 * "/debug" command
 * prints out relevant debug information
 * ex. output:
 * ... ... ...
 * player "name" info:
 *
 */
public class CommandDebug implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){
        if(sender instanceof Player player && player.isOp()){
            player.sendMessage(getPlayers().get(player.getUniqueId()).toString());
            return true;
        }
        return false;
    }

}

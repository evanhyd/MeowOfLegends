package unboxthecat.meowoflegends.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.UUID;

import static unboxthecat.meowoflegends.GameState.getPlayers;

/**
 * "/setmanaregenerationrate %d" command
 * sets manaRegenerationRate value
 */
public class CommandSetManaRegenerationRate implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){

        if(sender instanceof Player player){
            MOLEntity playerMOLEntity = getPlayers().get(player.getUniqueId());

            //no mana component
            ManaComponent manaComponent = (ManaComponent) playerMOLEntity.getComponent(ManaComponent.class);
            if(manaComponent == null){
                player.sendMessage(ChatColor.YELLOW +  "current entity " + player.getName() + " does not have mana component");
                return true;
            }

            //wrong number of argument
            if(args.length != 1){
                player.sendMessage(ChatColor.YELLOW + "invalid arguments for current mana");
                return true;
            }

            //invalid values for mana regeneration rate
            if(!args[0].matches("[-+]?[0-9]*\\.?[0-9]+")){
                player.sendMessage(ChatColor.YELLOW + "invalid values for current mana");
                return true;
            }

            //invalid values for mana regeneration rate
            double newManaRegenerationRate = Double.parseDouble(args[0]);
            if(newManaRegenerationRate < 0 ){
                player.sendMessage(ChatColor.YELLOW + "invalid values for current mana");
                return true;
            }

            //set mana regeneration rate to new value
            manaComponent.setManaRegenerationRate(newManaRegenerationRate);
            player.sendMessage(ChatColor.GREEN + "current mana has been changed to " + newManaRegenerationRate);
            return true;
        }

        //invalid commandSender attempts to use command
        return false;
    }
}

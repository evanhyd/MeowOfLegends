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


public class CommandSetMaxMana implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){

        if(sender instanceof Player player) {
            MOLEntity playerMOLEntity = getPlayers().get(player.getUniqueId());

            //no mana component
            ManaComponent manaComponent = playerMOLEntity.getComponent(ManaComponent.class);
            if(manaComponent == null){
                player.sendMessage(ChatColor.YELLOW +  "current entity " + player.getName() + " does not have mana component");
                return true;
            }

            //wrong number of argument
            if(args.length != 1){
                player.sendMessage(ChatColor.YELLOW + "invalid arguments for max mana");
                return true;
            }

            //invalid values for max mana
            if(!args[0].matches("[-+]?[0-9]*\\.?[0-9]+")){
                player.sendMessage(ChatColor.YELLOW + "invalid values for max mana");
                return true;
            }

            //invalid values for max mana
            double newMaxMana = Double.parseDouble(args[0]);
            if(newMaxMana < 0.1 ){
                player.sendMessage(ChatColor.YELLOW + "invalid values for max mana");
                return true;
            }

            //set max mana to new value
            manaComponent.setMaxMana(newMaxMana);
            player.sendMessage(ChatColor.GREEN + "max mana has been changed to " + newMaxMana);
            return true;
        }

        //invalid commandSender attempts to use command
        return false;
    }
}


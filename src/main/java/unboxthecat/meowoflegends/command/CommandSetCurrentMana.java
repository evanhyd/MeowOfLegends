package unboxthecat.meowoflegends.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import static unboxthecat.meowoflegends.GameState.getPlayers;

public class CommandSetCurrentMana implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){

        if(sender instanceof Player player){
            MOLEntity playerMOLEntity = getPlayers().get(player.getUniqueId());

            //no mana component
            ManaComponent manaComponent = playerMOLEntity.getComponent(ManaComponent.class);
            if(manaComponent == null){
                player.sendMessage(ChatColor.YELLOW +  "current entity " + player.getName() + " does not have mana component");
                return true;
            }

            //wrong number of argument
            if(args.length != 1){
                player.sendMessage(ChatColor.YELLOW + "invalid arguments for current mana");
                return true;
            }

            //invalid values for current mana
            if(!args[0].matches("[-+]?[0-9]*\\.?[0-9]+")){
                player.sendMessage(ChatColor.YELLOW + "invalid values for current mana");
                return true;
            }

            //invalid values for current mana
            double newCurrentMana = Double.parseDouble(args[0]);
            if(newCurrentMana < 0.0 || newCurrentMana > manaComponent.getMaxMana()){
                player.sendMessage(ChatColor.YELLOW + "invalid values for current mana");
                return true;
            }

            //set current mana to new value
            manaComponent.setCurrentMana(newCurrentMana);
            player.sendMessage(ChatColor.GREEN + "updated current mana to " + newCurrentMana);
            return true;
        }

        return false;
    }
}

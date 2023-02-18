package unboxthecat.meowoflegends.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static unboxthecat.meowoflegends.utility.GameState.getPlayers;


public class ManaComponentCommand implements CommandExecutor{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args){

        if(sender instanceof Player player) {
            MOLEntity playerMOLEntity = getPlayers().get(player.getUniqueId());

            ManaComponent manaComponent = playerMOLEntity.getComponent(ManaComponent.class);
            if(manaComponent == null){
                player.sendMessage(ChatColor.YELLOW + "You do not have any mana component");
                return true;
            }

            try {
                Method method = manaComponent.getClass().getDeclaredMethod(args[0], double.class);
                method.invoke(manaComponent, Double.parseDouble(args[1]));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                player.sendMessage("Failed to access such method");
            }
            return true;
        }

        return false;
    }
}


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
        //todo: change sender to only operator/admin to prevent normal player from using this command
        //player is sender
        if(sender instanceof Player){
            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();

            //getting info for message
            Map<UUID, MOLEntity> UUID_MOLEntity = getPlayers();
            MOLEntity playerMOLEntity = UUID_MOLEntity.get(playerUUID);

            Entity playerMOLEntityEntity = playerMOLEntity.getEntity();
            Map<String,MOLComponent> playerMOLEntityComponents = playerMOLEntity.getComponents();
            Map<String, MOLTag> playerMOLEntityTags = playerMOLEntity.getTags();

            //formatting entity info
            String entityInfo =
                    "Name    : " + playerMOLEntityEntity.getName() + "\n" +
                    "UUID    : " + playerMOLEntityEntity.getUniqueId() + "\n" +
                    "Vehicle : " + (playerMOLEntityEntity.getVehicle() == null ? "no vehicle" : playerMOLEntityEntity.getVehicle().toString()) + "\n" +
                    "FireTicks: " + playerMOLEntityEntity.getFireTicks()+ "\n" +
                    "FreezeTicks: " + playerMOLEntityEntity.getFreezeTicks() + "\n";

            //formatting components info
            StringBuilder componentsInfo = new StringBuilder();
            for (var entry : playerMOLEntityComponents.entrySet()){
                componentsInfo.append(entry.getKey()).append(" :\n").append(entry.getValue().toString()).append("\n");
            }

            //formatting tags info
            StringBuilder tagsInfo = new StringBuilder();
            for(var entry : playerMOLEntityTags.entrySet()){
                tagsInfo.append(entry.getKey()).append(" :\n").append(entry.getValue().toString()).append("\n");
            }


            //send message
            player.sendMessage(
                    ChatColor.BLUE + "Debug Info:\n" +
                    ChatColor.BLUE + "EntityInfo:\n" + ChatColor.LIGHT_PURPLE + entityInfo + "\n" +
                    ChatColor.BLUE +  "ComponentsInfo:\n" + ChatColor.LIGHT_PURPLE + componentsInfo + "\n" +
                    ChatColor.BLUE + "TagsInfo:\n" + ChatColor.LIGHT_PURPLE + tagsInfo + "\n");

            //stop execution
            return true;
        }

        //invalid commandSender attempts to use command
        return false;
    }

}

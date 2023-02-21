package unboxthecat.meowoflegends.handler;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import unboxthecat.meowoflegends.entity.character.Fizz;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.entity.character.Megumin;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Objects;
import java.util.UUID;

public class MOLPlayerLoginHandler implements Listener {

    @EventHandler
    public void loadMOLPlayerData(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        MOLEntity molEntity = null;
        if(player.hasPlayedBefore()) {
            molEntity = Objects.requireNonNull(GameState.getConfig().getSerializable(uuid.toString(), MOLEntity.class));
            molEntity.activate(player);
        } else {
            molEntity = new Fizz(player);
        }

        event.getPlayer().setGameMode(GameMode.CREATIVE);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.TRIDENT));

        GameState.getPlayers().put(uuid, molEntity);
    }

    @EventHandler
    public void saveMOLPlayerData(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        MOLEntity molEntity = GameState.getPlayers().get(uuid);

        GameState.getPlugin().getConfig().set(uuid.toString(), molEntity);
        GameState.getPlugin().saveConfig();

        molEntity.deactivate();
        GameState.getPlayers().remove(uuid);
    }
}

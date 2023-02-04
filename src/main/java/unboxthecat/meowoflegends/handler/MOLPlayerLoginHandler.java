package unboxthecat.meowoflegends.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.MOLEntity;

import java.util.Map;

public class MOLPlayerLoginHandler implements Listener {

    @EventHandler
    public void loadMOLPlayerData(PlayerJoinEvent event) {
        try
        {
            MOLEntity molEntity = null;

            if(event.getPlayer().hasPlayedBefore()) {
                String uuid = event.getPlayer().getUniqueId().toString();
                molEntity = new MOLEntity((Map<String, Object>)(GameState.getConfig().get(uuid)));
            } else {
                molEntity = new MOLEntity(event.getPlayer());
                molEntity.attachComponent(new ManaComponent(0.0, 50.0, 1.0));
            }

            GameState.getPlayers().put(event.getPlayer().getUniqueId(), molEntity);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void saveMOLPlayerData(PlayerQuitEvent event) {
        MOLEntity molEntity = GameState.getPlayers().get(event.getPlayer().getUniqueId());

        GameState.getPlugin().getConfig().set(String.valueOf(event.getPlayer().getUniqueId()), molEntity.serialize());
        GameState.getPlugin().saveConfig();

        molEntity.destroy();
        GameState.getPlayers().remove(event.getPlayer().getUniqueId());
    }
}

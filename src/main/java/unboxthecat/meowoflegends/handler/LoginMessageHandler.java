package unboxthecat.meowoflegends.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import unboxthecat.meowoflegends.GameState;

public class LoginMessageHandler implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            Bukkit.getOnlinePlayers().forEach(player ->
                player.sendTitle("Welcome",
                    event.getPlayer().getName(),
                        (int) GameState.secondToTick(1.0),
                        (int) GameState.secondToTick(5.0),
                        (int) GameState.secondToTick(1.0))
            );
        }
        event.setJoinMessage(ChatColor.AQUA + event.getPlayer().getName() + ChatColor.GREEN + " hopped on the server!");
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.AQUA + event.getPlayer().getName() + ChatColor.RED + " rage quited!");
    }
}

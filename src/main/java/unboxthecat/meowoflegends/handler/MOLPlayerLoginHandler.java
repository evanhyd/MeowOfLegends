package unboxthecat.meowoflegends.handler;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.BakuretsuMahou;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

public class MOLPlayerLoginHandler implements Listener {

    @EventHandler
    public void loadMOLPlayerData(PlayerJoinEvent event) {
        MOLEntity molEntity = null;
        if(event.getPlayer().hasPlayedBefore()) {
            String uuid = event.getPlayer().getUniqueId().toString();
            molEntity = GameState.getConfig().getSerializable(uuid, MOLEntity.class);
            assert(molEntity != null);
            molEntity.construct(event.getPlayer());
        } else {
            molEntity = new MOLEntity(event.getPlayer());
            molEntity.attachComponent(new ManaComponent(0.0, 50.0, 1.0));
            molEntity.attachComponent(new BakuretsuMahou(BakuretsuMahou.COOLDOWN_IN_SECONDS, BakuretsuMahou.MANA_PERCENT_COST));
        }
        GameState.getPlayers().put(event.getPlayer().getUniqueId(), molEntity);
        event.getPlayer().setGameMode(GameMode.CREATIVE);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
    }

    @EventHandler
    public void saveMOLPlayerData(PlayerQuitEvent event) {
        MOLEntity molEntity = GameState.getPlayers().get(event.getPlayer().getUniqueId());

        GameState.getPlugin().getConfig().set(event.getPlayer().getUniqueId().toString(), molEntity);
        GameState.getPlugin().saveConfig();

        molEntity.destroy();
        GameState.getPlayers().remove(event.getPlayer().getUniqueId());
    }
}

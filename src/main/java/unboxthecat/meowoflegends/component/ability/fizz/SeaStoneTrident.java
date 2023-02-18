package unboxthecat.meowoflegends.component.ability.fizz;

import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.TimerComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.tag.ability.fizz.SeaStoneTridentTag;

import java.util.Map;
import java.util.TreeMap;

//todo: proper damage and mana scaling for abilities
public class SeaStoneTrident extends AbilityComponent implements Listener {


    //serialize these data
    private MOLEntity owner;
    private final TimerComponent cooldown;
    private ManaComponent manaView;

    public SeaStoneTrident() {
        super(true);
        cooldown = new TimerComponent();
    }

    public SeaStoneTrident(Map<String, Object> data){
        super(true);
        cooldown = (TimerComponent) data.get("cooldown");
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("cooldown", cooldown);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner, Object... objects) {
        setUpAbilitySlot(owner);
        this.owner = owner;
        cooldown.onAttach(this.owner);
        manaView = owner.getComponent(ManaComponent.class); assert(manaView != null);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        HandlerList.unregisterAll(this);
        cooldown.onRemove(owner);
        this.owner = null;
    }

    @EventHandler
    private void trigger(PlayerInteractEvent event){
        if(event.getPlayer() != owner.getEntity()){
            return;
        }

        if(isUsingAbilitySlot(event.getPlayer()) && isUsingTrident(event.getAction()) && !onCoolDown() && hasMana()){

            applyCost();
            seaStoneTrident();
        }

    }

    private boolean onCoolDown(){
        return !cooldown.isReady();
    }

    private boolean hasMana(){
        return manaView.getMana() >= getAbilityManaCost();
    }

    private boolean isUsingTrident(Action action) {
        Player player = (Player) owner.getEntity();

        //not using trident
        if(player.getInventory().getItemInMainHand().getType() != Material.TRIDENT) return false;
        return (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private void applyCost(){
        manaView.consumeMana(getAbilityManaCost());
        cooldown.countDown(getAbilityCoolDownInSeconds());
    }

    private void seaStoneTrident(){
        if(!(owner.getEntity() instanceof LivingEntity)){
            return;
        }

        //attach tag for ability activation
        owner.attachTag(new SeaStoneTridentTag());

        //refresh attack cooldown(not working)
        Material weaponUsed = ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType();
        int refreshCooldown = 0;
        ((HumanEntity) owner.getEntity()).setCooldown(weaponUsed, refreshCooldown);


        //remove ability after 4s
        Plugin plugin = GameState.getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                owner.removeTag(SeaStoneTridentTag.class);
            }
        }, 4 * 20L);
    }

    double getAbilityManaCost(){
        //int level = (owner.getEntity() instanceof Player player ? player.getLevel() : 0);
        return 10;
    }

    double getAbilityCoolDownInSeconds(){
        int level = (owner.getEntity() instanceof Player player ? player.getLevel() : 0);
        return Math.max(2, 10 - level);
    }

    @Override
    public String toString() {
        return super.toString() + cooldown.toString();
    }
}

package unboxthecat.meowoflegends.component;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class UrchinStrike implements AbilityComponent, Listener {

    //these are all standard
    public static double coolDownInSeconds = 3;
    public static double manaCost = 20;
    public static double channelingTime = 2;
    public static double damage = 2;

    //serialize these data
    private MOLEntity owner;

    private CooldownComponent cooldownComponent;

    public UrchinStrike() {
        cooldownComponent = new CooldownComponent(coolDownInSeconds);
    }

    public UrchinStrike(final double initialCoolDownInSeconds, final double initialManaCost){
        coolDownInSeconds = initialCoolDownInSeconds;
        manaCost = initialManaCost;
        cooldownComponent = new CooldownComponent(coolDownInSeconds);
    }



    @Override
    public void onAttach(MOLEntity owner) {
        //initialize resources
        this.owner = owner;
        cooldownComponent.onAttach(this.owner);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner) {
        //uninitialized resources
        HandlerList.unregisterAll(this);
        cooldownComponent.onRemove(owner);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<String, Object>();
        //idk what data to save lmao
        return data;
    }


    private boolean onCoolDown(){
        return !cooldownComponent.isReady();
    }

    private boolean hasMana(){
        ManaComponent manaComponent = (ManaComponent) owner.getComponent(ManaComponent.class);
        return manaComponent.getCurrentMana() >= manaCost;
    }

    private boolean onHotBarSlot(int slotIndex){
        Player player = (Player) owner.getEntity();
        int hotBarSlot = player.getInventory().getHeldItemSlot();
        return hotBarSlot == slotIndex;
    }

    private boolean isUsingTrident(Action action){
        return (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }


    private void applyCost(){
        //mana
        ManaComponent manaComponent = owner.getComponent(ManaComponent.class);
        manaComponent.consumeMana(manaCost);

        //cool down
        cooldownComponent.restartTimer();
    }

    private void urchinStrike(){
        Vector direction = owner.getEntity().getLocation().getDirection();
        System.out.println("using urchin strike");
        System.out.println(direction);

    }

    @EventHandler
    private void trigger(PlayerInteractEvent event){
        if(!onCoolDown() &&
                hasMana() &&
                onHotBarSlot(0) &&
                isUsingTrident(event.getAction())
                                                ){
            applyCost();
            urchinStrike();
        }
    }

}

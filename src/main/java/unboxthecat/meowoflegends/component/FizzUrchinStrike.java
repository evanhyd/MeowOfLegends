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

public class FizzUrchinStrike implements AbilityComponent, Listener {

    //these are all standard
    public static double coolDownInSeconds;
    public static double manaPercentCost;
    public static double channelingTime;
    public static double damage;

    //serialize these data
    private MOLEntity owner;

    private CooldownComponent cooldownComponent;

    public FizzUrchinStrike() {
        cooldownComponent = new CooldownComponent(coolDownInSeconds);
    }

    public FizzUrchinStrike(final double initialCoolDownInSeconds, final double initialManaPercentCost){
        coolDownInSeconds = initialCoolDownInSeconds;
        manaPercentCost = initialManaPercentCost;
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
        return cooldownComponent.isReady();
    }

    private boolean hasMana(){
        ManaComponent manaComponent = (ManaComponent) owner.getComponent(ManaComponent.class);
        double currentManaPercent = (manaComponent.getCurrentMana() / manaComponent.getMaxMana()) * 100;
        return manaPercentCost < currentManaPercent;
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
        ManaComponent manaComponent = (ManaComponent) owner.getComponent(ManaComponent.class);
        manaComponent.consumeMana(manaPercentCost * manaComponent.getMaxMana());

        //cool down
        cooldownComponent.restartTimer();
    }

    private void urchinStrike(){
        Vector direction = owner.getEntity().getLocation().getDirection();
        System.out.println(direction);
    }

    @EventHandler
    private void trigger(PlayerInteractEvent event){
        if(!onCoolDown() && hasMana() &&
                onHotBarSlot(0) &&
                isUsingTrident(event.getAction())){
            applyCost();
            urchinStrike();
        }
    }

}

package unboxthecat.meowoflegends.component;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class UrchinStrike extends AbilityComponent implements Listener {

    //these are all standard
    public static double coolDownInSeconds = 3;
    public static double manaCost = 20;
    public static double channelingTime = 2;
    public static double damage = 2;

    //serialize these data
    private MOLEntity owner;

    private final CooldownComponent cooldownComponent;

    public UrchinStrike() {
        super(true);
        cooldownComponent = new CooldownComponent(coolDownInSeconds);
    }

    public UrchinStrike(final double initialCoolDownInSeconds, final double initialManaCost) {
        super(true);
        coolDownInSeconds = initialCoolDownInSeconds;
        manaCost = initialManaCost;
        cooldownComponent = new CooldownComponent(coolDownInSeconds);
    }



    @Override
    public void onAttach(MOLEntity owner) {
        setUpAbilitySlot(owner);
        this.owner = owner;
        cooldownComponent.onAttach(this.owner);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner) {
        HandlerList.unregisterAll(this);
        cooldownComponent.onRemove(owner);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<String, Object>();
        //idk what data to save
        return data;
    }


    private boolean onCoolDown(){
        return !cooldownComponent.isReady();
    }

    private boolean hasMana(){
        ManaComponent manaComponent = owner.getComponent(ManaComponent.class);
        return manaComponent != null && manaComponent.getMana() >= manaCost;
    }

    private boolean isUsingTrident(Action action) {
        Player player = (Player) owner.getEntity();

        //not using trident
        if(player.getInventory().getItemInMainHand().getType() != Material.TRIDENT) return false;
        return (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private RayTraceResult rayTracing(){
        Player player = (Player) owner.getEntity();
        Vector direction = player.getEyeLocation().getDirection();
        Location startLocation = player.getEyeLocation().add(direction.multiply(0.5));

        double maxDistance = 20;
        RayTraceResult result = player.getWorld().rayTraceEntities(startLocation, direction, maxDistance);

        return result;
    }


    private void applyCost(){
        ManaComponent manaComponent = owner.getComponent(ManaComponent.class);
        assert manaComponent != null;
        manaComponent.consumeMana(manaCost);
        cooldownComponent.restartTimer();
    }

    private void urchinStrike(RayTraceResult result){
        Player player = (Player) owner.getEntity();
        Vector direction = player.getLocation().getDirection();
        result.getHitEntity().setFireTicks(100);
        player.setVelocity(direction.multiply(10));

    }

    @EventHandler
    private void trigger(PlayerInteractEvent event){
        if(isUsingAbilitySlot(event.getPlayer()) && isUsingTrident(event.getAction()) && !onCoolDown() && hasMana()){

            RayTraceResult result = rayTracing();
            if(result == null || result.getHitEntity() == null){
                owner.getEntity().sendMessage(ChatColor.YELLOW + "must select target for urchin strike");
                return;
            }


            owner.getEntity().sendMessage(ChatColor.GREEN + "urchin strike hit " + result.getHitEntity().getName());
            applyCost();
            urchinStrike(result);
        }

    }

}

package unboxthecat.meowoflegends.component.ability.fizz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.component.generic.TimerComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class ChumTheWater extends AbilityComponent implements Listener {

    public static double coolDownInSeconds = 3;
    public static double manaCost = 20;
    public static double channelingTime = 2;
    public static double damage = 2;

    //serialize these data
    private MOLEntity owner;

    private final TimerComponent timerComponent;

    public ChumTheWater() {
        super(true);
        timerComponent = new TimerComponent();
    }

    public ChumTheWater(final double initialCoolDownInSeconds, final double initialManaCost) {
        super(true);
        coolDownInSeconds = initialCoolDownInSeconds;
        manaCost = initialManaCost;
        timerComponent = new TimerComponent();
    }
    public void onAttach(MOLEntity owner) {
        setUpAbilitySlot(owner);
        this.owner = owner;
        timerComponent.onAttach(this.owner);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    public void onRemove(MOLEntity owner) {
        HandlerList.unregisterAll(this);
        timerComponent.onRemove(owner);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        //todo: idk what to serialize
        Map<String, Object> data = new TreeMap<String, Object>();
        return data;
    }

    //something about making a shark jump up to eat a fish
    private boolean onCoolDown(){
        return !timerComponent.isReady();
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
    private void applyCost(){
        ManaComponent manaComponent = owner.getComponent(ManaComponent.class);
        assert manaComponent != null;
        manaComponent.consumeMana(manaCost);
        timerComponent.countDown(coolDownInSeconds);
    }

    private void chumTheWater(){
        Player player = (Player) owner.getEntity();

        Vector direction = player.getEyeLocation().getDirection();
        Location startLocation = player.getEyeLocation().add(direction.multiply(0.5));

        Entity fish = player.getWorld().spawnEntity(startLocation, EntityType.TROPICAL_FISH);
        fish.setVelocity(direction.multiply(10));

        Location endLocation = fish.getLocation();
        //now make something appear at endLocation


        //how to make fish go foward

    }
    @EventHandler
    private void trigger(PlayerInteractEvent event) {


        Player player = (Player) owner.getEntity();
    }



}

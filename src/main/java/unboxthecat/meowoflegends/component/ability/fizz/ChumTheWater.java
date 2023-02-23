package unboxthecat.meowoflegends.component.ability.fizz;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.utility.Timer;

import java.util.Map;
import java.util.TreeMap;

public class ChumTheWater extends AbilityComponent implements Listener {

    public static double coolDownInSeconds = 3;
    public static double manaCost = 20;
    public static double channelingTime = 2;
    public static double damage = 2;

    private MOLEntity owner;
    private final CooldownComponent abilityCooldown;

    public ChumTheWater() {
        super(true);
        abilityCooldown = new CooldownComponent(ChumTheWater.class.getSimpleName());
    }

    public ChumTheWater(final double initialCoolDownInSeconds, final double initialManaCost) {
        super(true);
        coolDownInSeconds = initialCoolDownInSeconds;
        manaCost = initialManaCost;
        abilityCooldown = new CooldownComponent(ChumTheWater.class.getSimpleName());
    }

    @Override
    public void onAttach(MOLEntity owner, Object... objects) {
        setUpAbilitySlot(owner);
        this.owner = owner;
        abilityCooldown.onRemove(owner);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        HandlerList.unregisterAll(this);
        abilityCooldown.onRemove(owner);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<String, Object>();
        return data;
    }

    //something about making a shark jump up to eat a fish
    private boolean hasMana(){
        ManaComponent manaComponent = owner.getComponent(ManaComponent.class);
        return manaComponent != null && manaComponent.getMana() >= manaCost;
    }
    private boolean isUsingTrident(Action action) {
        Player player = (Player) owner.getEntity();

        //not using trident
        if(player.getInventory().getItemInMainHand().getType() != Material.DIAMOND_SWORD) return false;
        return (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }
    private void applyCost(){
        ManaComponent manaComponent = owner.getComponent(ManaComponent.class);
        assert manaComponent != null;
        manaComponent.consumeMana(manaCost);
        abilityCooldown.run(coolDownInSeconds, false);
    }

    private void chumTheWater(){
        Player player = (Player) owner.getEntity();

        /*
        Vector direction = player.getEyeLocation().getDirection();
        Location startLocation = player.getEyeLocation().add(direction.multiply(0.5));

        Entity fish = player.getWorld().spawnEntity(startLocation, EntityType.TROPICAL_FISH);
        fish.setVelocity(direction.multiply(10));

        Location endLocation = fish.getLocation();
        */

        if(!(owner.getEntity() instanceof LivingEntity)){
            return;
        }

        Material weaponUsed = ((HumanEntity) owner.getEntity()).getInventory().getItemInMainHand().getType();

        ((HumanEntity) owner.getEntity()).sendMessage("weapon used is not null and is " + weaponUsed.toString());

        int refreshCooldown = 0;
        ((HumanEntity) owner.getEntity()).setCooldown(weaponUsed, refreshCooldown);
    }
    @EventHandler
    private void trigger(PlayerInteractEvent event) {
        if(event.getPlayer() != owner.getEntity()){
            return;
        }

        if(isUsingAbilitySlot(event.getPlayer()) && isUsingTrident(event.getAction()) && abilityCooldown.isReady() && hasMana()){

            applyCost();
            chumTheWater();
        }
    }
}

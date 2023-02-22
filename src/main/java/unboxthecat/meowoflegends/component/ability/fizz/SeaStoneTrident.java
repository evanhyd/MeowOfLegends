package unboxthecat.meowoflegends.component.ability.fizz;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.tag.ability.fizz.SeaStoneTridentTag;
import unboxthecat.meowoflegends.utility.Timer;

import java.util.Map;
import java.util.TreeMap;

//todo: proper damage and mana scaling for abilities
public class SeaStoneTrident extends AbilityComponent implements Listener {
    private MOLEntity owner;
    private final Timer cooldown;
    private ManaComponent manaView;

    public SeaStoneTrident() {
        super(true);
        cooldown = new Timer();
    }

    public SeaStoneTrident(Map<String, Object> data){
        super(true);
        cooldown = (Timer) data.get("cooldown");
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
        this.manaView = owner.getComponent(ManaComponent.class); assert(manaView != null);
        this.cooldown.resume();
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        HandlerList.unregisterAll(this);
        this.cooldown.pause();
        this.manaView = null;
        this.owner = null;
    }

    @EventHandler
    private void trigger(PlayerInteractEvent event){
        if(isOwner(event.getPlayer()) &&
           isUsingAbilitySlot(event.getPlayer()) &&
           isUsingTrident(event.getAction()) &&
           isCooldownReady() &&
           hasSufficientMana()) {

            applyAbilityCost();
            seaStoneTrident();
        }
    }

    private boolean isOwner(Entity entity) {
        return entity == owner.getEntity();
    }

    private boolean isUsingTrident(Action action) {
        return ((HumanEntity)owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.TRIDENT &&
                (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean hasSufficientMana(){
        return manaView.getMana() >= getAbilityManaCost();
    }

    private boolean isCooldownReady(){
        return cooldown.isIdling();
    }

    private void applyAbilityCost() {
        manaView.consumeMana(getAbilityManaCost());
        cooldown.run(getAbilityCoolDownInSeconds(), false);
    }

    private void seaStoneTrident() {
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(
            GameState.getPlugin(),
            () -> owner.removeTag(SeaStoneTridentTag.class),
            GameState.secondToTick(4.0)
        );
    }

    double getAbilityManaCost() {
        //int level = (owner.getEntity() instanceof Player player ? player.getLevel() : 0);
        return 10;
    }

    double getAbilityCoolDownInSeconds() {
        int level = (owner.getEntity() instanceof Player player ? player.getLevel() : 0);
        return Math.max(2, 10 - level);
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + cooldown.toString();
    }
}

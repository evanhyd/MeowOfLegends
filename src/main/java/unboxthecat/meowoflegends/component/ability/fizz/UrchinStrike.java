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
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;
import unboxthecat.meowoflegends.tag.ability.fizz.SeaStoneTridentTag;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;

//todo: proper damage and mana scaling for abilities
public class UrchinStrike extends AbilityComponent implements Listener {
    private MOLEntity owner;
    private final CooldownComponent abilityCooldown;
    private ManaComponent manaView;

    public UrchinStrike() {
        super(true);
        abilityCooldown = new CooldownComponent(UrchinStrike.class.getSimpleName());
    }

    public UrchinStrike(Map<String, Object> data){
        super(true);
        abilityCooldown = (CooldownComponent) data.get("abilityCooldown");
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("abilityCooldown", abilityCooldown);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner, Object... objects) {
        setUpAbilitySlot(owner);
        this.owner = owner;
        this.manaView = Objects.requireNonNull(owner.getComponent(ManaComponent.class));
        this.abilityCooldown.onAttach(owner);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        HandlerList.unregisterAll(this);
        this.abilityCooldown.onRemove(owner);
        this.manaView = null;
        this.owner = null;
    }

    @EventHandler
    private void trigger(PlayerInteractEvent event){

        if(event.getPlayer() == owner.getEntity() &&
           isUsingAbilitySlot(event.getPlayer()) &&
           isUsingTrident(event.getAction()) &&
           abilityCooldown.isReady() &&
           hasSufficientMana()) {

            LivingEntity target = (LivingEntity) getReachTarget();
            if (target != null) {
                applyAbilityCost();
                urchinStrike(target);
            }
        }
    }

    private boolean isUsingTrident(Action action) {
        return ((HumanEntity)owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.TRIDENT &&
                (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean hasSufficientMana(){
        return manaView.getMana() >= getAbilityManaCost();
    }

    @Nullable
    private Entity getReachTarget() {
        HumanEntity humanEntity = (HumanEntity) owner.getEntity();
        Vector direction = humanEntity.getEyeLocation().getDirection();
        Location startLocation = humanEntity.getEyeLocation().add(direction.multiply(0.5));

        double maxDistance = 20.0;
        Predicate<Entity> ignoreOwner = entity -> entity instanceof LivingEntity && entity != humanEntity;

        RayTraceResult hitTarget = humanEntity.getWorld().rayTrace(
            startLocation, direction, maxDistance, FluidCollisionMode.NEVER, true, 1.0,  ignoreOwner
        );

        return hitTarget == null ? null : hitTarget.getHitEntity();
    }

    private void urchinStrike(LivingEntity target) {
        HumanEntity player = (HumanEntity) owner.getEntity();
        Vector direction = player.getEyeLocation().getDirection();

        if(direction.getY() * 4 > 1){
            direction.setY((direction.getX() + direction.getZ()) / 8);
            direction.normalize();
        }

        //this doesn't do anything, since player's gravity if restore back to normal in the next few nano seconds
        //consider using Bukkit Task Scheduler
        player.setGravity(false);
        player.setVelocity(direction.multiply(4).clone());
        player.setGravity(true);

        //do damage
        target.damage(20.0, player);

        //on hit from SeaStoneTrident(fizz w)
        SeaStoneTridentTag tag = owner.getTag(SeaStoneTridentTag.class);
        if(tag != null){
            target.damage(20.0, player);
            //set some effect
            target.setFireTicks(100);
        }
        owner.removeTag(SeaStoneTridentTag.class);
    }

    private void applyAbilityCost(){
        manaView.consumeMana(getAbilityManaCost());
        abilityCooldown.run(getAbilityCoolDownInSeconds(), false);
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
        return super.toString() + "\n" + abilityCooldown.toString();
    }
}

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
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.TimerComponent;
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
    private final TimerComponent cooldown;
    private ManaComponent manaView;

    public UrchinStrike() {
        super(true);
        cooldown = new TimerComponent();
    }

    public UrchinStrike(Map<String, Object> data){
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
        this.cooldown.onAttach(owner);
        this.manaView = Objects.requireNonNull(owner.getComponent(ManaComponent.class));
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        HandlerList.unregisterAll(this);
        this.manaView = null;
        this.cooldown.onRemove(owner);
        this.owner = null;
    }

    @EventHandler
    private void trigger(PlayerInteractEvent event){

        if(isOwner(event.getPlayer()) &&
           isUsingAbilitySlot(event.getPlayer()) &&
           isUsingTrident(event.getAction()) &&
           isCooldownReady() &&
           hasSufficientMana()) {

            Entity targetEntity = getReachTarget();
            if (targetEntity != null) {
                applyAbilityCost();
                urchinStrike(targetEntity);
            }
        }

    }

    private boolean isOwner(Entity entity) {
        return entity == owner.getEntity();
    }

    private boolean isUsingTrident(Action action) {
        return ((HumanEntity)owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.TRIDENT &&
                (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean isCooldownReady(){
        return cooldown.isReady();
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

        if (hitTarget == null) {
            return null;
        } else {
            return hitTarget.getHitEntity();
        }
    }

    private void urchinStrike(Entity target) {
        Player player = (Player) owner.getEntity();
        Vector direction = player.getEyeLocation().getDirection();

        //do damage
        if(target instanceof LivingEntity){
            player.damage(20, target);
        }

        if(direction.getY() * 4 > 1){
            direction.setY((direction.getX() + direction.getZ()) / 8);
            direction.normalize();
        }

        //this doesn't do anything, since player's gravity if restore back to normal in the next few nano seconds
        //consider using Bukkit Task Scheduler
        player.setGravity(false);
        player.setVelocity(direction.multiply(4).clone());
        player.setGravity(true);

        //on hit from SeaStoneTrident(fizz w)
        SeaStoneTridentTag tag = owner.getTag(SeaStoneTridentTag.class);
        if(tag != null){
            player.damage(10, player);
            //set some effect
            target.setFireTicks(100);
        }
        owner.removeTag(SeaStoneTridentTag.class);
    }

    private void applyAbilityCost(){
        manaView.consumeMana(getAbilityManaCost());
        cooldown.countDown(getAbilityCoolDownInSeconds());
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

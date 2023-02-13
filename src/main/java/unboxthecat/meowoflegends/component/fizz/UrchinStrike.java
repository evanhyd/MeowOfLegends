package unboxthecat.meowoflegends.component.fizz;

import org.bukkit.*;
import org.bukkit.entity.Entity;
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
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.TimerComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class UrchinStrike extends AbilityComponent implements Listener {


    //serialize these data
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
    public void onAttach(MOLEntity owner) {
        setUpAbilitySlot(owner);
        this.owner = owner;
        cooldown.onAttach(this.owner);
        manaView = owner.getComponent(ManaComponent.class); assert(manaView != null);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner) {
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

            RayTraceResult result = rayTracing();
            if((result == null || result.getHitEntity() == null) ||
                    (result.getHitEntity() == owner.getEntity())){
                owner.getEntity().sendMessage(ChatColor.YELLOW + "must select target for urchin strike");
                return;
            }


            owner.getEntity().sendMessage(ChatColor.GREEN + "urchin strike hit " + result.getHitEntity().getName());
            applyCost();
            urchinStrike(result.getHitEntity());
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

    private double distanceSquared(Vector v1, Vector v2){
        return  (v1.getX() - v2.getX()) * (v1.getX() - v2.getX()) +
                (v1.getY() - v2.getY()) * (v1.getY() - v2.getY()) +
                (v1.getZ() - v2.getZ()) * (v1.getZ() - v2.getZ());
    }


    private RayTraceResult rayTracing(){
        Player player = (Player) owner.getEntity();
        Vector direction = player.getEyeLocation().getDirection();
        Location startLocation = player.getEyeLocation().add(direction.multiply(0.5));

        double maxDistance = 20;
        Predicate<Entity> ignorePlayer = entity -> {
            return entity.getUniqueId() != player.getUniqueId();
        };

        RayTraceResult resultEntity =  player.getWorld().rayTraceEntities(startLocation, direction, maxDistance, ignorePlayer);
        RayTraceResult resultBlock = player.getWorld().rayTraceBlocks(startLocation, direction, maxDistance);

        if(resultEntity == null || resultEntity.getHitEntity() == null) return null;
        Vector resultEntityLocation = resultEntity.getHitEntity().getLocation().toVector();

        if(resultBlock == null) return resultEntity;
        Vector resultBlockLocation = resultBlock.getHitPosition();

        double distanceSquaredStartEntity = distanceSquared(startLocation.toVector(), resultEntityLocation);
        double distanceSquaredStartBlock = distanceSquared(startLocation.toVector(), resultBlockLocation);

        if(distanceSquaredStartEntity < distanceSquaredStartBlock) return resultEntity;
        else return null;
    }

    private void applyCost(){
        manaView.consumeMana(getAbilityManaCost());
        cooldown.countDown(getAbilityCoolDownInSeconds());
    }

    private void urchinStrike(Entity target){
        Player player = (Player) owner.getEntity();
        Vector direction = player.getEyeLocation().getDirection();

        //set some effect
        target.setFireTicks(100);

        //do damage
        if(target instanceof LivingEntity){
            ((LivingEntity) target).damage(20);
        }

        if(direction.getY() * 4 > 1){
            direction.setY((direction.getX() + direction.getZ()) / 8);
            direction.normalize();
        }


        player.setGravity(false);
        player.setVelocity(direction.multiply(4).clone());
        player.setGravity(true);
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

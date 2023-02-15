package unboxthecat.meowoflegends.component.ability.fizz;

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
import unboxthecat.meowoflegends.tag.ability.fizz.SeaStoneTridentTag;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;

//todo: proper damage and mana scaling for abilities
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
        manaView = Objects.requireNonNull(owner.getComponent(ManaComponent.class));
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

    private RayTraceResult rayTracing(){
        Player player = (Player) owner.getEntity();
        Vector direction = player.getEyeLocation().getDirection();
        Location startLocation = player.getEyeLocation().add(direction.multiply(0.5));

        double maxDistance = 20;
        Predicate<Entity> ignorePlayer = entity -> entity instanceof LivingEntity && entity != player;

        RayTraceResult resultEntity =  player.getWorld().rayTraceEntities(startLocation, direction, maxDistance, ignorePlayer);
        RayTraceResult resultBlock = player.getWorld().rayTraceBlocks(startLocation, direction, maxDistance);

        if(resultEntity == null || resultEntity.getHitEntity() == null) return null;
        Location resultEntityLocation = resultEntity.getHitEntity().getLocation();

        if(resultBlock == null) return resultEntity;
        Vector resultBlockLocation = resultBlock.getHitPosition();

        double distanceSquaredStartEntity = startLocation.distanceSquared(resultEntityLocation);
        double distanceSquaredStartBlock = startLocation.toVector().distanceSquared(resultBlockLocation);

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

        //do damage
        if(target instanceof LivingEntity){
            player.damage(20, target);
        }

        if(direction.getY() * 4 > 1){
            direction.setY((direction.getX() + direction.getZ()) / 8);
            direction.normalize();
        }


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

package unboxthecat.meowoflegends.component.ability.megumin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.utility.GameState;
import unboxthecat.meowoflegends.component.base.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.component.generic.TimerComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.*;

public class BouncingFireball extends AbilityComponent implements Listener {

    private MOLEntity owner;
    private final TimerComponent cooldown;
    private final Map<Projectile, Integer> fireballs;
    private ManaComponent manaView;

    public BouncingFireball() {
        super(true);
        this.cooldown = new TimerComponent();
        this.fireballs = new HashMap<>();
    }

    public BouncingFireball(Map<String, Object> data) {
        super(true);
        this.cooldown = (TimerComponent) data.get("cooldown");
        this.fireballs = new HashMap<>();
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
        TimerComponent.TimerCallback callback = () -> {
            if (owner.getEntity() instanceof Player player) {
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        };
        this.cooldown.onAttach(owner, callback);
        this.manaView = Objects.requireNonNull(owner.getComponent(ManaComponent.class));;
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner, Object... objects) {
        HandlerList.unregisterAll(this);
        this.manaView = null;
        this.cooldown.onRemove(owner);
        this.owner = null;
        this.fireballs.forEach((fireball, bouncedTime) -> fireball.remove());
        this.fireballs.clear();
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (isOwner(event.getPlayer()) &&
            isUsingAbilitySlot(event.getPlayer()) &&
            isUsingBlazeRod(event.getAction()) &&
            isManaSufficient() &&
            isCooldownReady()) {

            applyAbilityCost();
            launchFireball();
        }
    }

    private boolean isOwner(Entity user) {
        return user == owner.getEntity();
    }

    private boolean isUsingBlazeRod(Action action) {
        return ((HumanEntity)owner.getEntity()).getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD &&
                (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean isManaSufficient() {
        return this.manaView.getMana() >= getAbilityManaCost();
    }

    private boolean isCooldownReady() {
        return cooldown.isReady();
    }

    private void applyAbilityCost() {
        this.manaView.consumeMana(getAbilityManaCost());
        this.cooldown.countDown(getAbilityCooldown());
    }

    private void launchFireball() {
        LivingEntity user = (LivingEntity) owner.getEntity();
        Fireball fireball = (Fireball) user.getWorld().spawnEntity(user.getEyeLocation(), EntityType.FIREBALL);
        fireball.setYield(getAbilityExplosionPower());
        fireball.setIsIncendiary(true);
        fireball.setDirection(user.getEyeLocation().getDirection());
        fireballs.put(fireball, 0);
    }

    @EventHandler
    public void onFireballHit(ProjectileHitEvent event) {
        if (fireballs.containsKey(event.getEntity())) {
            Fireball fireball = (Fireball) event.getEntity();
            int bounce = fireballs.get(fireball);
            fireballs.remove(fireball);

            if (event.getHitBlockFace() != null && bounce < getAbilityMaxBounce()) {
                Vector incoming = event.getEntity().getVelocity();
                Vector normal = event.getHitBlockFace().getDirection();
                Vector outgoing = incoming.add(normal.multiply(-2.0 * incoming.dot(normal)));

                fireball.setYield(0.0f);
                fireball.setIsIncendiary(false);

                fireball = (Fireball) owner.getEntity().getWorld().spawnEntity(fireball.getLocation(), EntityType.FIREBALL);
                fireball.setYield(getAbilityExplosionPower());
                fireball.setIsIncendiary(true);
                fireball.setDirection(outgoing);
                fireballs.put(fireball, bounce + 1);
            } else if (event.getHitEntity() == owner.getEntity()) {
                event.setCancelled(true);
            }
        }
    }

    private double getAbilityManaCost() {
        return 20.0;
    }

    public double getAbilityCooldown() {
        return 3.0;
    }

    private int getAbilityMaxBounce() {
        return 2;
    }

    private float getAbilityExplosionPower() {
        return 5.0f;
    }

    @Override
    public String toString() {
        return super.toString() + cooldown.toString();
    }
}

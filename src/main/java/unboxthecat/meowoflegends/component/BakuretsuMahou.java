package unboxthecat.meowoflegends.component;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class BakuretsuMahou implements AbilityComponent, Listener {
    MOLEntity owner;
    CooldownComponent cooldownComponent;
    double manaPercentCost;
    float explosionPower;

    public BakuretsuMahou(double cooldownInSeconds, double manaPercentCost, float explosionPower) {
        this.cooldownComponent = new CooldownComponent(cooldownInSeconds);
        this.manaPercentCost = manaPercentCost;
        this.explosionPower = explosionPower;
    }

    public BakuretsuMahou(Map<String, Object> data) {
        manaPercentCost = (double) data.get("manaPercentCost");
        cooldownComponent = new CooldownComponent((Map<String, Object>) data.get("cooldownComponent"));
        explosionPower = (float) data.get("explosionPower");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("manaPercentCost", manaPercentCost);
        data.put("cooldownComponent", cooldownComponent.serialize());
        data.put("explosionPower", explosionPower);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner) {
        this.owner = owner;
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner) {
        HandlerList.unregisterAll(this);
        cooldownComponent.onRemove(owner);
    }

    @EventHandler
    public void trigger(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() == owner.getEntity().getUniqueId() &&
            player.getInventory().getHeldItemSlot() == 0 &&
            (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            if (isApplicable()) {
                applyCost();
                activate();
            }
        }
    }

    public boolean isApplicable() {
        ManaComponent manaComponent = (ManaComponent) owner.getComponent(ManaComponent.class);
        return manaComponent != null && manaComponent.getCurrentMana() >= manaComponent.getMaxMana() * manaPercentCost && cooldownComponent.isReady();
    }

    public void applyCost() {
        ManaComponent manaComponent = (ManaComponent) owner.getComponent(ManaComponent.class);
        manaComponent.consumeMana(manaComponent.getMaxMana() * manaPercentCost);
        cooldownComponent.restartTimer();
    }

    @Override
    public void activate() {
        Player player = (Player)(owner.getEntity());
        LargeFireball fireball = owner.getEntity().getWorld().spawn(player.getEyeLocation(), LargeFireball.class);
        fireball.setDirection(player.getEyeLocation().getDirection());
        fireball.setIsIncendiary(true);
        fireball.setGlowing(true);
        fireball.setYield(10.0F);

//        (Player)(owner.getEntity()).get
//        World world  = owner.getEntity().getLocation().getWorld();
//        if (world != null) {
//            world.createExplosion(owner.getEntity().getLocation(), 100.0F, true, true, owner.getEntity());
//        }
    }

    //Tunable values
    public static final double COOLDOWN_SECONDS = 300.0;
    public static final float EXPLOSION_POWER = 100.0F;
    public static final double MANA_PERCENT_COST = 1.0;

}
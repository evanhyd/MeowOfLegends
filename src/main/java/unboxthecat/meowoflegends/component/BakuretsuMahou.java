package unboxthecat.meowoflegends.component;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import unboxthecat.meowoflegends.GameState;
import unboxthecat.meowoflegends.component.generic.AbilityComponent;
import unboxthecat.meowoflegends.component.generic.CooldownComponent;
import unboxthecat.meowoflegends.component.generic.ManaComponent;
import unboxthecat.meowoflegends.entity.generic.MOLEntity;

import java.util.Map;
import java.util.TreeMap;

public class BakuretsuMahou implements AbilityComponent, Listener {
    //Tunable values
    public static final double COOLDOWN_IN_SECONDS = 1.0;
    public static final double MANA_PERCENT_COST = 0.05;
    public static final int EXPLOSION_RANGE = 100;
    public static final int EXPLOSION_MAGMA_RADIUS = 10;
    public static final int EXPLOSION_MAGMA_HEIGHT = 2;
    public static final int EXPLOSION_LAVA_RADIUS = 7;
    public static final int EXPLOSION_LAVA_HEIGHT = 10;
    public static final double EXPLOSION_LAVA_DURATION_IN_SECONDS = 10.0;
    public static final int EXPLOSION_DAMAGE_RADIUS = 10;
    public static final int EXPLOSION_DAMAGE_HEIGHT = 10;
    public static final double EXPLOSION_DAMAGE = 10.0;


    private MOLEntity owner;
    private CooldownComponent cooldownComponent;
    private double manaPercentCost;

    public BakuretsuMahou(double cooldownInSeconds, double manaPercentCost) {
        this.cooldownComponent = new CooldownComponent(cooldownInSeconds);
        this.manaPercentCost = manaPercentCost;
    }

    public BakuretsuMahou(Map<String, Object> data) {
        manaPercentCost = (double) data.get("manaPercentCost");
        cooldownComponent = (CooldownComponent)(data.get("cooldownComponent"));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new TreeMap<>();
        data.put("manaPercentCost", manaPercentCost);
        data.put("cooldownComponent", cooldownComponent);
        return data;
    }

    @Override
    public void onAttach(MOLEntity owner) {
        this.owner = owner;
        cooldownComponent.onAttach(owner);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameState.getPlugin());
    }

    @Override
    public void onRemove(MOLEntity owner) {
        HandlerList.unregisterAll(this);
        cooldownComponent.onRemove(owner);
    }

    @EventHandler
    public void trigger(PlayerInteractEvent event) {

        if (isOwner(event.getPlayer()) &&
            isUsingBlazeRod(event.getAction()) &&
            isTargetingBlock() &&
            isManaSufficient() &&
            isCooldownReady()) {

            explode();
            applyCost();
        }
    }

    private boolean isOwner(Player player) {
        return player.getUniqueId().equals(owner.getEntity().getUniqueId());
    }

    private boolean isUsingBlazeRod(Action action) {
        Player player = (Player)(owner.getEntity());
        return player.getInventory().getHeldItemSlot() == 0 &&
               player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD &&
               (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    private boolean isTargetingBlock() {
        return ((Player)owner.getEntity()).getTargetBlockExact(100, FluidCollisionMode.NEVER) != null;
    }

    private boolean isManaSufficient() {
        ManaComponent manaComponent = (ManaComponent) owner.getComponent(ManaComponent.class);
        return manaComponent != null && manaComponent.getCurrentMana() >= manaComponent.getMaxMana() * manaPercentCost;
    }

    private boolean isCooldownReady() {
        return cooldownComponent.isReady();
    }

    private void explode() {

        Block block = ((Player)owner.getEntity()).getTargetBlockExact(EXPLOSION_RANGE, FluidCollisionMode.NEVER);
        if (block == null) {
            return;
        }

        World world = block.getWorld();
        Location location = block.getLocation();

        //create magma block base
        double magmaCreationDelayInSeconds = 0.0;
        for (int radius = 0; radius < EXPLOSION_MAGMA_RADIUS; ++radius) {
            for (int x = 0; x < radius; ++x) {
                int z = (int) Math.sqrt(radius * radius - x * x);
                for (int y = 0; y < EXPLOSION_MAGMA_HEIGHT; ++y) {

                    int finalX = x;
                    int finalY = y;

                    location.add(finalX, finalY, -z);

                    Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {
                        world.setType((int) location.getX() +  finalX, (int) location.getY() + finalY, (int) location.getZ() - z, Material.MAGMA_BLOCK);
                        world.setType((int) location.getX() + finalX, (int) location.getY() - finalY, (int) location.getZ() - z, Material.MAGMA_BLOCK);
                        world.setType((int) location.getX() - finalX, (int) location.getY() + finalY, (int) location.getZ() - z, Material.MAGMA_BLOCK);
                        world.setType((int) location.getX() - finalX, (int) location.getY() - finalY, (int) location.getZ() - z, Material.MAGMA_BLOCK);
                    }, GameState.secondToTick(magmaCreationDelayInSeconds));

                    Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {
                        world.setType((int) location.getX() +  finalX, (int) location.getY() + finalY, (int) location.getZ() - z, Material.STONE);
                        world.setType((int) location.getX() + finalX, (int) location.getY() - finalY, (int) location.getZ() - z, Material.STONE);
                        world.setType((int) location.getX() - finalX, (int) location.getY() + finalY, (int) location.getZ() - z, Material.STONE);
                        world.setType((int) location.getX() - finalX, (int) location.getY() - finalY, (int) location.getZ() - z, Material.STONE);
                    }, GameState.secondToTick(EXPLOSION_LAVA_DURATION_IN_SECONDS));
                }
            }

            magmaCreationDelayInSeconds += 0.05;
        }

        for (int radius = 0; radius < EXPLOSION_LAVA_RADIUS; ++radius) {
            for (int x = 0; x < radius; ++x) {
                int z = (int) Math.sqrt(radius * radius - x * x);
                for (int y = -EXPLOSION_MAGMA_HEIGHT; y < EXPLOSION_LAVA_HEIGHT; ++y) {

                    int finalX = x;
                    int finalY = y;

                    world.setType((int) location.getX() +  finalX, (int) location.getY() + finalY, (int) location.getZ() + z, Material.LAVA);
                    world.setType((int) location.getX() + finalX, (int) location.getY() - finalY, (int) location.getZ() + z, Material.LAVA);
                    world.setType((int) location.getX() - finalX, (int) location.getY() + finalY, (int) location.getZ() + z, Material.LAVA);
                    world.setType((int) location.getX() - finalX, (int) location.getY() - finalY, (int) location.getZ() + z, Material.LAVA);

                    Bukkit.getScheduler().runTaskLater(GameState.getPlugin(), () -> {
                        world.setType((int) location.getX() +  finalX, (int) location.getY() + finalY, (int) location.getZ() + z, Material.AIR);
                        world.setType((int) location.getX() + finalX, (int) location.getY() - finalY, (int) location.getZ() + z, Material.AIR);
                        world.setType((int) location.getX() - finalX, (int) location.getY() + finalY, (int) location.getZ() + z, Material.AIR);
                        world.setType((int) location.getX() - finalX, (int) location.getY() - finalY, (int) location.getZ() + z, Material.AIR);
                    }, GameState.secondToTick(EXPLOSION_LAVA_DURATION_IN_SECONDS));
                }
            }
        }

        world.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.5f);

        var entities = world.getNearbyEntities(location, EXPLOSION_DAMAGE_RADIUS, EXPLOSION_DAMAGE_RADIUS, EXPLOSION_DAMAGE_HEIGHT);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).damage(EXPLOSION_DAMAGE, owner.getEntity());
            }
        }
    }

    private void applyCost() {
        ManaComponent manaComponent = (ManaComponent) owner.getComponent(ManaComponent.class);
        manaComponent.consumeMana(manaComponent.getMaxMana() * manaPercentCost);
        cooldownComponent.restartTimer();
    }
}

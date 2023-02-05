package unboxthecat.meowoflegends.handler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathMessageHandler implements Listener {
    @EventHandler
    void sendDeathMessage(PlayerDeathEvent event){

        EntityDamageEvent lastDamageEvent = event.getEntity().getLastDamageCause();
        if(lastDamageEvent == null){
            return;
        }

        //death message
        EntityDamageEvent.DamageCause damageCause = lastDamageEvent.getCause();
        switch (damageCause){
            case BLOCK_EXPLOSION:{
                event.setDeathMessage(event.getEntity().getName() + "got hit by a meteorite\n");
                break;
            }
            case CONTACT:{
                event.setDeathMessage(event.getEntity().getName() + " was squished by kittens");
                break;
            }
            case CRAMMING:{
                event.setDeathMessage(event.getEntity().getName() + " was crammed away into a closet");
                break;
            }
            case CUSTOM:{
                event.setDeathMessage(event.getEntity().getName() + " died of unknown causes");
                break;
            }
            case ENTITY_ATTACK:{
                event.setDeathMessage(event.getEntity().getName() + " took an L");
                break;
            }
            case ENTITY_EXPLOSION:{
                event.setDeathMessage(ChatColor.BOLD + "CREEPERS! aww man");
                break;
            }
            case DRAGON_BREATH:{
                event.setDeathMessage(event.getEntity().getName() + " was melted by dragon's breath");
                break;
            }
            case DROWNING:{
                event.setDeathMessage(event.getEntity().getName() + " hit an iceberg and drowned");
                break;
            }
            case ENTITY_SWEEP_ATTACK:{
                event.setDeathMessage(event.getEntity().getName() + " was swept out of this world by a broom");
                break;
            }
            case FALL:{
                event.setDeathMessage(event.getEntity().getName() + " believed they could fly");
                break;
            }
            case FALLING_BLOCK:{
                event.setDeathMessage(event.getEntity().getName() + " was caught in rain of blocks");
                break;
            }
            case FIRE:{
                event.setDeathMessage(event.getEntity().getName() + " was roasted for dinner");
                break;
            }
            case FIRE_TICK:{
                event.setDeathMessage(event.getEntity().getName() + " was burnt to ashes");
                break;
            }
            case FLY_INTO_WALL:{
                event.setDeathMessage(event.getEntity().getName() + " flew into a wall");
                break;
            }
            case FREEZE:{
                event.setDeathMessage(event.getEntity().getName() + " was turned into ice cubes");
                break;
            }
            case HOT_FLOOR:{
                event.setDeathMessage(event.getEntity().getName() + " legs melted");
                break;
            }
            case LAVA:{
                event.setDeathMessage(event.getEntity().getName() + " melted into a pool of lava");
                break;
            }
            case LIGHTNING:{
                event.setDeathMessage(event.getEntity().getName() + " was blessed by lightning");
                break;
            }
            case MAGIC:{
                event.setDeathMessage(event.getEntity().getName() + " died to voldemort");
                break;
            }
            case MELTING:{
                event.setDeathMessage(event.getEntity().getName() + " turned into liquid");
                break;
            }
            case POISON:{
                event.setDeathMessage(event.getEntity().getName() + " was cursed to death");
                break;
            }
            case PROJECTILE:{
                event.setDeathMessage(event.getEntity().getName() + " was hit by projectiles");
                break;
            }
            case SONIC_BOOM:{
                event.setDeathMessage(event.getEntity().getName() + " was turned into a juke box");
                break;
            }
            case STARVATION:{
                event.setDeathMessage(event.getEntity().getName() + " died while searching for food");
                break;
            }
            case SUFFOCATION:{
                event.setDeathMessage(event.getEntity().getName() + " suffocated in body pillows");
                break;
            }
            case SUICIDE:{
                event.setDeathMessage(event.getEntity().getName() + " killed themself");
                break;
            }
            case THORNS:{
                event.setDeathMessage(event.getEntity().getName() + " died to punching a hedgehog");
                break;
            }
            case VOID:{
                event.setDeathMessage(event.getEntity().getName() + " was trapped in a dark abyss");
                break;
            }
            case WITHER:{
                event.setDeathMessage(event.getEntity().getName() + " withered away");
                break;
            }
            default: {
                event.setDeathMessage(event.getEntity().getName() + "died");
            }
        }

        //death coordinates
        Location deathLocation = event.getEntity().getLocation();
        double deathLocationX = event.getEntity().getLocation().getX();
        double deathLocationY = event.getEntity().getLocation().getY();
        double deathLocationZ = event.getEntity().getLocation().getZ();
        event.getEntity().sendMessage(ChatColor.BOLD + "Death Location: " +
                String.format("%.0f %.0f %.0f", deathLocationX, deathLocationY, deathLocationZ));
    }
}

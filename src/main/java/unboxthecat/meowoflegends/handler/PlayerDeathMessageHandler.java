package unboxthecat.meowoflegends.handler;

import org.bukkit.ChatColor;
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
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + "got hit by a meteorite\n");
                break;
            }
            case CONTACT:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was squished by kittens");
                break;
            }
            case CRAMMING:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was crammed away into a closet");
                break;
            }
            case CUSTOM:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " died of unknown causes");
                break;
            }
            case ENTITY_ATTACK:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " took an L");
                break;
            }
            case ENTITY_EXPLOSION:{
                event.setDeathMessage(ChatColor.RED + "CREEPERS! aww man");
                break;
            }
            case DRAGON_BREATH:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was melted by dragon's breath");
                break;
            }
            case DROWNING:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " hit an iceberg and drowned");
                break;
            }
            case ENTITY_SWEEP_ATTACK:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was swept out of this world by a broom");
                break;
            }
            case FALL:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " believed they could fly");
                break;
            }
            case FALLING_BLOCK:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was caught in rain of blocks");
                break;
            }
            case FIRE:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was roasted for dinner");
                break;
            }
            case FIRE_TICK:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was burnt to ashes");
                break;
            }
            case FLY_INTO_WALL:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " flew into a wall");
                break;
            }
            case FREEZE:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was turned into ice cubes");
                break;
            }
            case HOT_FLOOR:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " melted");
                break;
            }
            case LAVA:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " melted into a pool of lava");
                break;
            }
            case LIGHTNING:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was blessed by lightning");
                break;
            }
            case MAGIC:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " died to voldemort");
                break;
            }
            case MELTING:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " turned into liquid");
                break;
            }
            case POISON:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was cursed to death");
                break;
            }
            case PROJECTILE:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was hit by projectiles");
                break;
            }
            case SONIC_BOOM:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was turned into a juke box");
                break;
            }
            case STARVATION:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " died while searching for food");
                break;
            }
            case SUFFOCATION:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED +" suffocated in body pillows");
                break;
            }
            case SUICIDE:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " killed themself");
                break;
            }
            case THORNS:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " died to punching a hedgehog");
                break;
            }
            case VOID:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() +
                        ChatColor.RED + " was trapped in a dark abyss");
                break;
            }
            case WITHER:{
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() + ChatColor.RED + " withered away");
                break;
            }
            default: {
                event.setDeathMessage(ChatColor.RED + event.getEntity().getName() + ChatColor.RED + "died");
            }
        }

        //death coordinates
        double deathLocationX = event.getEntity().getLocation().getX();
        double deathLocationY = event.getEntity().getLocation().getY();
        double deathLocationZ = event.getEntity().getLocation().getZ();
        event.getEntity().sendMessage(ChatColor.BOLD + "Death Location: " +
                String.format("%.0f %.0f %.0f", deathLocationX, deathLocationY, deathLocationZ));
    }
}

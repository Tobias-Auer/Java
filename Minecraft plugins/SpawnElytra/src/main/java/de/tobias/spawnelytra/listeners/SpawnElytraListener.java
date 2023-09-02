package de.tobias.spawnelytra.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import static org.bukkit.block.BlockFace.DOWN;

public class SpawnElytraListener implements Listener {

    private final Set<Player> flyingPlayers = new HashSet<>();
    private final int spawnRadius;

    public SpawnElytraListener(Plugin plugin) {
        this.spawnRadius = 20;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Objects.requireNonNull(Bukkit.getWorld("world")).getPlayers()) {


                if (player.getGameMode() == GameMode.SURVIVAL) {
                    boolean isInSpawn = isInSpawnRadius(player);
                    player.setAllowFlight(isInSpawn);
                    if (isInSpawn) player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§bSpawnElytra available §r| §3press <space> twice to fly"));
                }
                if (flyingPlayers.contains(player) && !player.getLocation().getBlock().getRelative(DOWN).getType().isAir()) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.setGliding(false);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> flyingPlayers.remove(player), 5);
                }
            }
        }, 0, 3);
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL) return;
        if (!isInSpawnRadius(player)) return;

        event.setCancelled(true);
        player.setGliding(true);
        flyingPlayers.add(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (event.getEntityType() == EntityType.PLAYER
                && (event.getCause() == EntityDamageEvent.DamageCause.FALL
                || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL)
                && flyingPlayers.contains(player)){
            event.setCancelled(true);
            flyingPlayers.remove(player);
        }
    }


    @EventHandler
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (event.getEntityType() == EntityType.PLAYER && flyingPlayers.contains(player)) event.setCancelled(true);
    }

    private boolean isInSpawnRadius(Player player) {
        World world = player.getWorld();
        if (!player.getWorld().equals(world)) return false;
        return world.getSpawnLocation().distance(player.getLocation()) <= spawnRadius;
    }
}

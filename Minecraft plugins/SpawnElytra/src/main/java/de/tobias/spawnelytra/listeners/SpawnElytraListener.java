package de.tobias.spawnelytra.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.block.BlockFace.DOWN;

public class SpawnElytraListener implements Listener {

    private final Set<Player> flyingPlayers = new HashSet<>();

    public SpawnElytraListener(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getWorld("world").getPlayers()) {
                if (player.getGameMode() == GameMode.SURVIVAL && isInSpawnRadius(player)) {
                    player.setAllowFlight(true);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§bSpawnElytra available §r| §3press <space> twice to fly"));
                }
                if (flyingPlayers.contains(player) && !player.getLocation().getBlock().getRelative(DOWN).getType().isAir()) {
                    player.setFlying(false);
                    player.setGliding(false);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        flyingPlayers.remove(player);
                    }, 20);
                    flyingPlayers.remove(player);
                }
            }
        }, 0, 3);
    }

    @EventHandler
    public void onTryFly(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

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
        if (!flyingPlayers.contains(player)) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onToggleFly(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (flyingPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }

    public boolean isInSpawnRadius(Player player) {
        if (!player.getWorld().getName().equals("world")) {
            return false;
        }

        int spawnRadius = 20;


        return (player.getLocation().distance(player.getWorld().getSpawnLocation()) <= spawnRadius &&
                !player.getLocation().getBlock().getRelative(DOWN).getType().isAir());
    }
}

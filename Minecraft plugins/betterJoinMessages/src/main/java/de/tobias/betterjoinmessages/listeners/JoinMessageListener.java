package de.tobias.betterjoinmessages.listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

/*
    §0: Schwarz
    §1: Dunkelblau
    §2: Dunkelgrün
    §3: Dunkelaqua
    §4: Dunkelrot
    §5: Dunkelviolett
    §6: Gold
    §7: Grau
    §8: Dunkelgrau
    §9: Blau
    §a-d: die helle Version der Farbe hinter der jeweils zugehörigen Ziffer
    §e: Gelb
    §f: Weiß
 */

public class JoinMessageListener implements Listener {
    private final YamlConfiguration config;

    public JoinMessageListener(YamlConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        config.contains("otherMsgs." + playerName);
        if (config.contains("otherMsgs." + playerName)) {
            event.setJoinMessage(Objects.requireNonNull(config.getString("otherMsgs." + playerName + ".msgJoin")).replace("%player%", playerName));
            return;
        }

        event.setJoinMessage(Objects.requireNonNull(config.getString("msgJoin")).replace("%player%", playerName));

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        config.contains("otherMsgs." + playerName);
        if (config.contains("otherMsgs." + playerName)) {
            event.setQuitMessage(Objects.requireNonNull(config.getString("otherMsgs." + playerName + ".msgQuit")).replace("%player%", playerName));
            return;
        }

        event.setQuitMessage(Objects.requireNonNull(config.getString("msgQuit")).replace("%player%", playerName));

    }
}

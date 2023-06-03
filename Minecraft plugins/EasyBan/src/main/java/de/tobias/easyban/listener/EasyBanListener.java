package de.tobias.easyban.listener;

import de.tobias.easyban.EasyBan;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.io.File;

public class EasyBanListener implements Listener {
    public EasyBanListener(YamlConfiguration config, File configFile, EasyBan easyBan) {

    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandPreprocess(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equalsIgnoreCase("/ban")) {
            event.setCancelled(true);
        }

    }
}

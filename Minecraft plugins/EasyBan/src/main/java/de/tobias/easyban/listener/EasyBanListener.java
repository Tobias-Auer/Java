package de.tobias.easyban.listener;

import de.tobias.easyban.EasyBan;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;

public class EasyBanListener implements Listener {
    private final YamlConfiguration config;
    private final File configFile;

    public EasyBanListener(YamlConfiguration config, File configFile, EasyBan easyBan) {
        this.config = config;
        this.configFile = configFile;
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandPreprocess(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (event.getMessage().equalsIgnoreCase("/ban")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        ArrayList<String> bannedPLayers = new ArrayList<String>(config.getKeys(false));
        if (bannedPLayers.contains(event.getPlayer().getUniqueId().toString())) {
            String message = config.get("ban-message").toString();
            message = message.replace("{0}", config.getStringList(uuid).get(1));
            message = message.replace("{1}", config.getStringList(uuid).get(2));

            String bannedUntilTimeString = config.getStringList(uuid).get(3);// ban time


            Date nowTimeObject = new Date();

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
            String nowTimeString = format.format(nowTimeObject);

            getLogger().info("Date and time in Berlin: " + nowTimeString);
            getLogger().info("Date until banned: "  + bannedUntilTimeString);


            message = message.replace("{2}", bannedUntilTimeString);
            UUID adminUuid = UUID.fromString(config.getStringList(uuid).get(0));
            message = message.replace("{3}", Bukkit.getOfflinePlayer(adminUuid).getName());

            try {
                Date bannedUntilTimeObject = format.parse(bannedUntilTimeString);
                if (nowTimeObject.before(bannedUntilTimeObject)) {
                    event.getPlayer().kickPlayer(message);
                } else if (nowTimeObject.after(bannedUntilTimeObject)) {
                    config.set(uuid, null);
                    config.save(configFile);
                    event.getPlayer().sendTitle(config.getString("welcomeback-message1"), config.getString("welcomeback-message2"), 10, 250, 70);
                }
            } catch (ParseException | IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

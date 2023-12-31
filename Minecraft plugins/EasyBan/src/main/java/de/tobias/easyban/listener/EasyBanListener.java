package de.tobias.easyban.listener;

import de.tobias.easyban.EasyBan;
import de.tobias.easyban.SQLiteConnector;
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
    private final SQLiteConnector connector;

    public EasyBanListener(SQLiteConnector connector, YamlConfiguration config, EasyBan easyBan) {
        this.config = config;
        this.connector = connector;
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
        ArrayList<String> bannedPLayers = connector.getAllBannedUuids();
        if (bannedPLayers.contains(event.getPlayer().getUniqueId().toString())) {

            Map<String, String> banEntryMap = connector.readBanEntry(uuid);
            String banned_uuid = banEntryMap.get("uuid");
            String admin_uuid = banEntryMap.get("admin");
            String reason = banEntryMap.get("reason");
            String start = banEntryMap.get("start");
            String end = banEntryMap.get("end");
            getLogger().info("Retrieved ban entry: " + banned_uuid + ", " + admin_uuid + ", " + reason + ", " + start + ", " + end);


            String message = config.get("ban-message").toString();
            message = message.replace("{0}", reason);
            message = message.replace("{1}", start);

            String bannedUntilTimeString = end;// ban time


            Date nowTimeObject = new Date();

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
            String nowTimeString = format.format(nowTimeObject);

            getLogger().info("Date and time in Berlin: " + nowTimeString);
            getLogger().info("Date until banned: "  + bannedUntilTimeString);



            message = message.replace("{2}", bannedUntilTimeString);
            UUID adminUuid = UUID.fromString(admin_uuid);
            message = message.replace("{3}", Bukkit.getOfflinePlayer(adminUuid).getName());

            try {
                Date bannedUntilTimeObject = format.parse(bannedUntilTimeString);
                if (nowTimeObject.before(bannedUntilTimeObject)) {
                    event.getPlayer().kickPlayer(message);
                } else if (nowTimeObject.after(bannedUntilTimeObject)) {
                    connector.removeBanEntry(uuid);
                    event.getPlayer().sendTitle(config.getString("welcomeback-message1"), config.getString("welcomeback-message2"), 10, 250, 70);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

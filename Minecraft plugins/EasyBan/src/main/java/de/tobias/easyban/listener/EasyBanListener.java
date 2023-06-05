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

            String dateString = config.getStringList(uuid).get(3);// ban time


            Date now = new Date();

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
            String date = format.format(now);

            System.out.println("Date and time in Berlin: " + date);


            message = message.replace("{2}", dateString);
            UUID adminUuid = UUID.fromString(config.getStringList(uuid).get(0));
            message = message.replace("{3}", Bukkit.getOfflinePlayer(adminUuid).getName());

            try {
                Date futureTime = format.parse(dateString);
                Date nowTime = format.parse(nowString);
                getLogger().info(nowTime.toString());
                getLogger().info(futureTime.toString());
                if (nowTime.before(futureTime)) {
                    System.out.println("Die aktuelle Zeit liegt vor der zukünftigen Zeit.");
                } else if (nowTime.after(futureTime)) {
                    System.out.println("Die aktuelle Zeit liegt nach der zukünftigen Zeit.");
                } else {
                    System.out.println("Die aktuelle Zeit und die zukünftige Zeit sind gleich.");
                }
                if (futureTime.after(nowTime)) {
                    event.getPlayer().kickPlayer(message);
                }else {
                    config.set(uuid, null);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }
    }
}

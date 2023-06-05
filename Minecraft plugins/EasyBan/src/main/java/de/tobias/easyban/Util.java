package de.tobias.easyban;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

public class Util {
    public static HashMap<UUID, String> getAllPlayers() {
        HashMap<UUID, String> playerMap = new HashMap<>();
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            playerMap.put(offlinePlayer.getUniqueId(), offlinePlayer.getName());
        }

        return playerMap;
    }
    public static String returnTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}

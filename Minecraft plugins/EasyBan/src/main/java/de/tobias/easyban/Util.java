package de.tobias.easyban;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

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
    public static boolean isValidUUID(String input) {
        // Regular expression pattern for Minecraft UUIDs
        String uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

        // Create a Pattern object with the regex pattern
        Pattern pattern = Pattern.compile(uuidPattern, Pattern.CASE_INSENSITIVE);

        // Check if the input matches the UUID pattern
        return pattern.matcher(input).matches();
    }
}

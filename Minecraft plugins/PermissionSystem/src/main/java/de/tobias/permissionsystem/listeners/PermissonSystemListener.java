package de.tobias.permissionsystem.listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class PermissonSystemListener implements Listener {

    private final YamlConfiguration config;
    private final File configFile;





    // TODO: Implement commands
    // TODO: Implement API methods

    public PermissonSystemListener(YamlConfiguration config, File configFile) {
        this.config = config;
        this.configFile = configFile;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        getLogger().info("START CHECKING");
        if (!config.getKeys(false).contains(playerUuid.toString())) {
            config.set(playerUuid + ".permissons", new ArrayList<>());
            config.save(configFile);
        }
        addPermission(playerUuid, "gamemmode");
        addPermission(playerUuid, "shutdown");
        getLogger().info(checkPermission(playerUuid, "gamemmode"));;
        getLogger().info(checkPermission(playerUuid, "shutdown"));;
        getLogger().info(checkPermission(playerUuid, "lol"));
        removePermission(playerUuid, "gamemmode");
        getLogger().info(checkPermission(playerUuid, "gamemmode"));;
    }


    public void addPermission(UUID uuid, String permission) {
        try {
            List<String> existingList = config.getStringList(uuid + ".permissons");
            existingList.add(permission);
            config.set(uuid + ".permissons", existingList);

            config.save(configFile);
            getLogger().info("Added permission: " + permission);
            getLogger().info("UUID: " + uuid);
            getLogger().info("Player: " + Bukkit.getOfflinePlayer(uuid).getName());
        } catch (IOException e) {
            getLogger().info("FAILED TO ADD: " + permission);
            getLogger().info("ERROR: " + e.getMessage());

        }
    }

    public void removePermission(UUID uuid, String permission) {
        try {
            List<String> existingList = config.getStringList(uuid + ".permissons");
            existingList.remove(permission);
            config.set(uuid + ".permissons", existingList);

            config.save(configFile);
            getLogger().info("REMOVED permission: " + permission);
            getLogger().info("UUID: " + uuid);
            getLogger().info("Player: " + Bukkit.getOfflinePlayer(uuid).getName());
        } catch (IOException e) {
            getLogger().info("FAILED TO REMOVE: " + permission);
            getLogger().info("UUID: " + uuid);
            getLogger().info("Player: " + Bukkit.getOfflinePlayer(uuid).getName());
            getLogger().info("ERROR: " + e.getMessage());
        }
    }

    public String checkPermission(UUID uuid, String permission) {
        List<String> existingList = config.getStringList(uuid + ".permissons");
        getLogger().info("Checking "+ permission + "for "+ Bukkit.getOfflinePlayer(uuid).getName());
        return String.valueOf(existingList.contains(permission));
    }
}

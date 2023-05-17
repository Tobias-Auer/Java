package de.tobias.permissionsystem.listeners;

import de.tobias.permissionsystem.PermissionSystem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;

public class PermissonSystemListener implements Listener{

    private final YamlConfiguration config;
    private final File configFile;
    private final PermissionSystem plugin;
    private final AttachmentManager attachmentManager;

    public PermissonSystemListener(YamlConfiguration config, File configFile, AttachmentManager attachmentManager, PermissionSystem plugin) {
        this.config = config;
        this.configFile = configFile;
        this.plugin = plugin;
        this.attachmentManager = attachmentManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();
        UUID playerUuid = player.getUniqueId();
        if (!config.getKeys(false).contains(playerUuid.toString())) {
            config.set(String.valueOf(playerUuid), new ArrayList<>());
            config.save(configFile);
        }
        PermissionAttachment attachment = player.addAttachment(plugin);
        for (String permission : config.getStringList(playerUuid.toString())) {
            attachment.setPermission(permission, true);
            getLogger().info("Added permission to player (onJoin): " + permission);
        }

        player.recalculatePermissions();
        attachmentManager.addAttachment(player, attachment);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        attachmentManager.removeAttachment(player);
    }

}
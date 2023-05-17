package de.tobias.permissionsystem;

import de.tobias.permissionsystem.listeners.AttachmentManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getPlayer;

public class PermissonSystemMain implements CommandExecutor {
    private final YamlConfiguration config;
    private final File configFile;
    private final PermissionSystem plugin;
    private final AttachmentManager attachmentManager;

    public PermissonSystemMain(YamlConfiguration config, File configFile, AttachmentManager attachmentManager, PermissionSystem plugin) {
        this.config = config;
        this.configFile = configFile;
        this.plugin = plugin;
        this.attachmentManager = attachmentManager;
    }
    public void addPermission(UUID uuid, String permission) {
        try {
            List<String> existingList = config.getStringList(uuid.toString());
            existingList.add(permission);
            config.set(uuid.toString(), existingList);
            config.save(configFile);
            getLogger().info("Added permission to file: " + permission);
            getLogger().info("UUID: " + uuid);
            getLogger().info("Player: " + Bukkit.getOfflinePlayer(uuid).getName());
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                PermissionAttachment attachment = player.addAttachment(plugin);
                attachment.setPermission(permission, true);
                attachmentManager.addAttachment(player, attachment);
                player.recalculatePermissions();
                getLogger().info("Added permission to player: " + permission);
            } else {
                getLogger().info("Player is offline. Permission will be applied as soon as they join the server.");
            }
        } catch (IOException e) {
            getLogger().info("Failed to add permission: " + permission);
            getLogger().info("UUID: " + uuid);
            getLogger().info("Error: " + e.getMessage());
        }
    }


    public void removePermission(UUID uuid, String permission) {
        try {
            List<String> existingList = config.getStringList(uuid + ".permissons");
            existingList.remove(permission);
            config.set(uuid.toString(), existingList);

            config.save(configFile);
            getLogger().info("REMOVED permission from file: " + permission);
            getLogger().info("UUID: " + uuid);
            getLogger().info("Player: " + Bukkit.getOfflinePlayer(uuid).getName());

            Player player = Bukkit.getPlayer(uuid);
            if (player.isOnline()) {
                PermissionAttachment attachment = player.addAttachment(plugin);
                attachment.unsetPermission(permission);
                player.recalculatePermissions();
                attachmentManager.removeAttachment(player);
                getLogger().info("Removed permission from player: " + permission);
            } else {
                getLogger().info("Player is offline. Permission will be removed as soon as he join the server");
            }
        } catch (IOException e) {
            getLogger().info("FAILED TO REMOVE: " + permission);
            getLogger().info("UUID: " + uuid);
            getLogger().info("Player: " + Bukkit.getOfflinePlayer(uuid).getName());
            getLogger().info("ERROR: " + e.getMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (!(sender instanceof Player)) {
            player = getPlayer("_Tobias4444");
        }

        Player senderPlayer = (Player) sender;

        if (args.length < 3) {
            senderPlayer.sendMessage("Verwendung: /permission <add | remove> <player> <permission>");
            return true;
        }

        String action = args[0].toLowerCase();
        player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage("§4WARNING: §rPlayer not found.");
            return true;
        }
        String permission = args[2];

        switch (action) {
            case "add":
                addPermission(player.getUniqueId(), permission);
                senderPlayer.sendMessage("Berechtigung §b" + permission + " §rwurde hinzugefügt.");
                break;
            case "remove":
                removePermission(player.getUniqueId(), permission);
                senderPlayer.sendMessage("Berechtigung §4" + permission + " §rwurde entfernt.");
                break;
            case "check":
                    getLogger().info(String.valueOf(player.isPermissionSet("minecraft.command.gamemode")));
                break;
            default:
                senderPlayer.sendMessage("Verwendung: /permission <add | remove> <player> <permission> Action: " + action);
                break;
        }

        return true;
    }


}

package de.tobias.permissionsystem;

import de.tobias.permissionsystem.listeners.AttachmentManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class Util {
    private static YamlConfiguration config = null;
    private static File configFile = null;
    private static PermissionSystem plugin = null;
    private static AttachmentManager attachmentManager = null;

    public Util(YamlConfiguration config, File configFile, PermissionSystem plugin, AttachmentManager attachmentManager) {
        Util.config = config;
        Util.configFile = configFile;
        Util.plugin = plugin;
        Util.attachmentManager = attachmentManager;
    }

    public static boolean addPermissionToPlayer(UUID uuid, String permission, List<String> existingList) {
        try {
            List<String> playerPerms = config.getStringList(uuid.toString());
            if (playerPerms.contains(permission)) {
                throw new IllegalStateException("Permission already assigned!");
            }
            if (existingList.contains(permission)) {
                playerPerms.add(permission);
                config.set(uuid.toString(), playerPerms);
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
                    getLogger().info("Player is offline. Permission will be applied as soon as he joins the server.");
                }
            } else {
                throw new IllegalStateException("Permission not found!");
            }
        } catch (Exception e) {
            getLogger().info("Failed to add permission: " + permission);
            getLogger().info("UUID: " + uuid);
            getLogger().info("Error: " + e.getMessage());
            return false;
        }
        return true;
    }


    public static boolean removePermissionFromPlayer(UUID uuid, String permission) {
        try {
            List<String> playerPerms = config.getStringList(uuid.toString());
            if (playerPerms.contains(permission)) {

                playerPerms.remove(permission);
                config.set(uuid.toString(), playerPerms);
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
            } else {
                throw new IllegalStateException("Permission not found!");
            }
        } catch (Exception e) {
            getLogger().info("FAILED TO REMOVE: " + permission);
            getLogger().info("UUID: " + uuid);
            getLogger().info("Player: " + Bukkit.getOfflinePlayer(uuid).getName());
            getLogger().info("ERROR: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean checkPermission(UUID uuid, String permission) {
        OfflinePlayer offlinePlayer = getPlayerFromUuid(uuid);

        if (offlinePlayer == null || !offlinePlayer.isOnline()) {
            getLogger().info("checkPermission for " + permission + " failed, because " + offlinePlayer + " is not online");
            return false;
        }

        Player player = (Player) offlinePlayer;
        return player.isPermissionSet(permission);
    }

    public static OfflinePlayer getPlayerFromUuid(UUID uuid) {
        try {
            return Bukkit.getOfflinePlayer(uuid);

        } catch (Exception e) {
            getLogger().info(e.toString());
            return null;
        }
    }

    public static UUID getUuidFromPlayer(String player) {
        try {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
            UUID uuid = null;
            if (offlinePlayer.hasPlayedBefore()) {
                uuid = offlinePlayer.getUniqueId();
            }
            return uuid;

        } catch (Exception e) {
            getLogger().info(e.toString());
            return null;
        }
    }

    public static List<String> listAllPermissionFromPlayer(UUID uuid) {
        List<String> list = null;
        try {
            list = config.getStringList(uuid.toString());
            Player player = (Player) getPlayerFromUuid(uuid);
            for (PermissionAttachmentInfo attachment : player.getEffectivePermissions()) {
                if (attachment.getValue()) {
                    list.add(attachment.getPermission());
                }
            }
        } catch (Exception e) {
            getLogger().info("listAllPermissionFromPlayer failed, because " + uuid + " caused following exception");
            getLogger().info(e.toString());
        }

        return list;
    }

    public static boolean check(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            sender.sendMessage("Player recognized");
            sender.sendMessage(String.valueOf(player.hasPermission("admin.manage.playerPermission")));
            sender.sendMessage(String.valueOf(player.isPermissionSet("admin.manage.playerPermission")));
            sender.sendMessage(String.valueOf(player.hasPermission("lolwtf")));
            sender.sendMessage(String.valueOf(player.isPermissionSet("lolwtf")));
            return player.hasPermission("admin.manage.playerPermission");
        }

        return false;
    }
}
//case "removepermission":
//        if (existingList.contains(permission)) {
//        existingList.remove(permission);
//        config.set("permissions", existingList);
//        for (String uuidString : config.getKeys(false)) {
//        List<String> permissions = config.getStringList(uuidString);
//        permissions.remove(permission);
//        config.set(uuidString, permissions);
//        }
//        sender.sendMessage("Berechtigung §4" + permission + " §rwurde gelöscht und von allen Spielern entfernt!.");
//        } else {
//        sender.sendMessage("Berechtigung §4" + permission + " §rwurde nicht gefunden.");
//        }
//        break;


//        if (!existingList.contains(permission)) {
//            existingList.add(permission);
//            config.set("permissions", existingList);
//            Permission permissionObj = new Permission(permission);
//            Bukkit.getServer().getPluginManager().addPermission(permissionObj);
//       }
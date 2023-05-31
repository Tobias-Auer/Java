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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

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

    public boolean addPermissionToPlayer(UUID uuid, String permission, List<String> existingList) {
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
                    PermissionAttachment attachment = attachmentManager.getAttachment(uuid);
                    attachment.setPermission(permission, true);

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


    public boolean removePermissionFromPlayer(UUID uuid, String permission) {
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
                    PermissionAttachment attachment = attachmentManager.getAttachment(uuid);
                    getLogger().info("REMOVED permission from attachment: " + attachment);
                    getLogger().info("Player: " + permission);
                    getLogger().info(attachment.getPermissions().toString());
                    attachment.unsetPermission(permission);

                    player.recalculatePermissions();
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


    public OfflinePlayer getPlayerFromUuid(UUID uuid) {
        try {
            return Bukkit.getOfflinePlayer(uuid);

        } catch (Exception e) {
            getLogger().info(e.toString());
            return null;
        }
    }

    public UUID getUuidFromPlayer(String player) {
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

    public List<String> listAllPermissionFromPlayer(UUID uuid) {
        List<String> list = new ArrayList<>();
        try {
            Player player = Bukkit.getPlayer(uuid);
            for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
                String permission = attachmentInfo.getPermission();
                if (attachmentInfo.getValue()) {
                    if (permission.startsWith("admin.")) {
                        list.add(attachmentInfo.getPermission());
                    }
                }
            }
            return list;
        } catch (Exception e) {
            getLogger().info("listAllPermissionFromPlayer failed, because " + uuid + " caused following exception");
            getLogger().info(e.toString());
        }

        return list;
    }

    public boolean check(CommandSender sender, String permission) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            return player.hasPermission(permission);
        }
        return false;
    }

    public boolean isValidUUID(String input) {
        // Regular expression pattern for Minecraft UUIDs
        String uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";

        // Create a Pattern object with the regex pattern
        Pattern pattern = Pattern.compile(uuidPattern, Pattern.CASE_INSENSITIVE);

        // Check if the input matches the UUID pattern
        return pattern.matcher(input).matches();
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
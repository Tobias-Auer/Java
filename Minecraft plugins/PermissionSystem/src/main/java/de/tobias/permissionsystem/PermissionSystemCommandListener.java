package de.tobias.permissionsystem;

import de.tobias.permissionsystem.listeners.AttachmentManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionSystemCommandListener implements CommandExecutor, TabCompleter {
    private final YamlConfiguration config;
    private final File configFile;
    private final Util util;

    public PermissionSystemCommandListener(YamlConfiguration config, File configFile, AttachmentManager attachmentManager, PermissionSystem plugin) {
        this.config = config;
        this.configFile = configFile;
        this.util = new Util(config, configFile, plugin, attachmentManager);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("perm")) {
            if (!util.check(sender, "admin.manage.playerpermission")) {
                sender.sendMessage("§4[ERROR] Keine Berechtigung!");
                return true;
            }

            String permission = null;
            String action;
            UUID targetUuid;
            if (args.length != 3 && !args[0].equalsIgnoreCase("list")) {
                sender.sendMessage("Verwendung: /perm <add | remove | list> <player> <permission>");
                return true;
            }
            action = args[0].toLowerCase();
            targetUuid = util.getUuidFromPlayer(args[1]);
            if (targetUuid == null) {
                sender.sendMessage("§4[ERROR] Der Spieler " + args[1] + " war noch nicht auf dem Server!");
                return true;
            }
            if (!action.equals("list")) {
                permission = args[2];
            }

            List<String> existingList = config.getStringList("permissions");
            switch (action) {
                case "add":
                    if (!existingList.contains(permission)) {
                        sender.sendMessage("§6[WARNING] §4Berechtigung §b" + permission + " §4konnte nicht hinzugefügt werden, da diese nicht existiert!");
                        break;
                    }
                    if (util.addPermissionToPlayer(targetUuid, permission, existingList)) {
                        sender.sendMessage("§7[INFO] §rBerechtigung §b" + permission + " §rwurde hinzugefügt.");
                        break;
                    }
                    sender.sendMessage("§4[ERROR] Berechtigung §b" + permission + " §4konnte dem Spieler nicht hinzugefügt werden!");
                    break;

                case "remove":
                    if (util.removePermissionFromPlayer(targetUuid, permission)) {
                        sender.sendMessage("Berechtigung §4" + permission + " §rwurde entfernt.");
                        break;
                    }
                    sender.sendMessage("§4[ERROR] Berechtigung §b" + permission + " §4konnte dem Spieler nicht entfernt werden!");
                    break;

                case "list":
                    List<String> list = util.listAllPermissionFromPlayer(targetUuid);
                    if (list != null) {
                        sender.sendMessage("§4=====================");
                        for (String key : list) {
                            if (key.startsWith("admin.manage.")) {
                                sender.sendMessage("§5" + key);
                            } else {
                                sender.sendMessage(key);
                            }
                        }
                        sender.sendMessage("§4=====================");
                    }
                    break;
                default:
                    sender.sendMessage("Verwendung: /permission <add | remove | check> <player> <permission>");
                    break;
            }
            try {
                config.save(configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (command.getName().equals("perm")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("admin.manage.playerpermission")) {
                    return new ArrayList<>(); // Return an empty list to prevent autocompletion
                }
            }
            if (args.length == 1) {
                completions.add("list");
                completions.add("add");
                completions.add("remove");
            } else if (args.length == 2) {
                for (String uuid : config.getKeys(false)) {
                    if (util.isValidUUID(uuid)) {
                        completions.add(util.getPlayerFromUuid(UUID.fromString(uuid)).getName());
                    }
                }
            } else if (args.length == 3) {
                List<String> existingList = config.getStringList("permissions");
                Player player = (Player) sender;
                List<String> playerPerms = config.getStringList(player.getUniqueId().toString());

                if (args[0].equals("add")) {
                    for (String permission : existingList) {
                        if (!playerPerms.contains(permission)) {
                            completions.add(permission);
                        }
                    }
                } else if (args[0].equals("remove")) {
                    completions.addAll(playerPerms);
                }
            }
        }

        return completions;

    }
}
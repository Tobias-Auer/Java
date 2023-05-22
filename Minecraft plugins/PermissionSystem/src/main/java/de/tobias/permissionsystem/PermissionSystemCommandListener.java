package de.tobias.permissionsystem;

import de.tobias.permissionsystem.listeners.AttachmentManager;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;

public class PermissionSystemCommandListener implements CommandExecutor {
    private final YamlConfiguration config;
    private final File configFile;
    private final PermissionSystem plugin;
    private final AttachmentManager attachmentManager;
    private final Util util;

    public PermissionSystemCommandListener(YamlConfiguration config, File configFile, AttachmentManager attachmentManager, PermissionSystem plugin) {
        this.config = config;
        this.configFile = configFile;
        this.plugin = plugin;
        this.attachmentManager = attachmentManager;
        this.util = new Util(config, configFile, plugin, attachmentManager);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("permission")) {
            if (sender instanceof Player) {
                Player playerObject = (Player) sender;
                if (!playerObject.isPermissionSet("admin.manage.playerPermissions")) {
                    getLogger().info("Sender has no permission");
                    sender.sendMessage("§4[ERROR] Du hast keine Berechtigung diesen Befehl auszuführen!");
                    return true;
                }
            } else if (!(sender.getClass().getName().equals("com.destroystokyo.paper.console.TerminalConsoleCommandSender"))) {
                getLogger().info(sender.getClass().getName());
                getLogger().info("Sender has no permission");
                sender.sendMessage("§4[ERROR] Du hast keine Berechtigung diesen Befehl auszuführen!");
                return true;
            }
        }else {
            getLogger().info("Not a player or ServerConsole!");
        }
        getLogger().info("Sender has permission");
        String permission = null;
        String action;
        UUID targetUuid;
        if (args.length != 3 && !args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("Verwendung: /permission <add | remove | check> <player> <permission>");
            return true;
        }
        action = args[0].toLowerCase();
        targetUuid = Util.getUuidFromPlayer(args[1]);
        if (targetUuid == null) {
            sender.sendMessage("§4[ERROR] Der Spieler " + args[1] + " war noch nicht auf dem Server!");
        }
        if (!action.equals("list")) {
            permission = args[2];
        }


//        player = Bukkit.getPlayer(args[1]);
//        if (player == null) {
//            sender.sendMessage("§7[ERROR] §rPlayer not found.");
//            return true;
//        }


        List<String> existingList = config.getStringList("permissions");
        switch (action) {
            case "add":
                if (!existingList.contains(permission)) {
                    sender.sendMessage("§6[WARNING] §4Berechtigung §b" + permission + " §4konnte nicht hinzugefügt werden, da diese nicht existiert!");
                    break;
                }
                if (Util.addPermissionToPlayer(targetUuid, permission, existingList)) {
                    sender.sendMessage("§7[INFO] §rBerechtigung §b" + permission + " §rwurde hinzugefügt.");
                    break;
                }
                sender.sendMessage("§4[ERROR] Berechtigung §b" + permission + " §4konnte dem Spieler nicht hinzugefügt werden!");
                break;

            case "remove":
                if (Util.removePermissionFromPlayer(targetUuid, permission)) {
                    sender.sendMessage("Berechtigung §4" + permission + " §rwurde entfernt.");
                    break;
                }
                sender.sendMessage("§4[ERROR] Berechtigung §b" + permission + " §4konnte dem Spieler nicht entfernt werden!");
                break;

            case "check":
//                sender.sendMessage(String.valueOf(Util.checkPermission(targetUuid, permission)));
                break;
            case "list":
                List<String> list = Util.listAllPermissionFromPlayer(targetUuid);
                if (list != null) {
                    sender.sendMessage("§4=====================");
                    for (String key : list) {
                        sender.sendMessage(key);
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

}

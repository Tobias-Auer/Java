package de.tobias.easyban;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getPlayer;

public class EasyBanCommandListener implements TabCompleter, CommandExecutor {
    private final String[] reasons;
    private final SQLiteConnector connector;
    private final YamlConfiguration config;
    public EasyBanCommandListener(SQLiteConnector connector, YamlConfiguration config, EasyBan easyBan) {
        this.config = config;
        this.reasons = new String[]{"Hacking", "Griefing", "Spam", "Advertisement", "Insult", "Disrespect", "Money trading", "Misinformation", "Other"};
        this.connector = connector;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (command.getName().equals("ban")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("admin.ban")) {
                    return new ArrayList<>();
                }
            }

            if (args.length == 1) {
                HashMap<UUID, String> players = Util.getAllPlayers();
                ArrayList<String> bannedUuids = connector.getAllBannedUuids();
                for (String playerName : players.values()) {
                    OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(playerName);
                    if (!bannedUuids.contains(offlineplayer.getUniqueId().toString())) {
                        completions.add(playerName);
                    }
                }
            }
            if (args.length == 2) {
                completions.addAll(List.of(reasons));
            }
            if (args.length == 3) {
                completions.add("minutes");
            }
            if (args.length == 4) {
                completions.add("hours");
            }
            if (args.length == 5) {
                completions.add("days");
            }
            if (args.length == 6) {
                completions.add("months");
            }
        } else if (command.getName().equals("unban")) {
            getLogger().info(command.getName());
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("admin.ban")) {
                    return new ArrayList<>();
                }
            }
            if (args.length == 1) {
                ArrayList<String> bannedUuids = connector.getAllBannedUuids();
                for (String playerUuid : bannedUuids) {
                    if (Util.isValidUUID(playerUuid)) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUuid));
                        completions.add(offlinePlayer.getName());
                    }
                }
            }
        }

        return completions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("admin.ban")) {
                sender.sendMessage("§4[ERROR]No permission");
                return true;
            }
        }
        if (command.getName().equals("ban")) {
            try {
                Player player = getPlayer(args[0]);
                OfflinePlayer offlinePlayer = null;
                if (player == null) {
                    offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                }
                Player senderPlayer = getPlayer(sender.getName());

                int minutes = Integer.parseInt(args[2]);
                int hours = Integer.parseInt(args[3]);
                int days = Integer.parseInt(args[4]);
                int months = Integer.parseInt(args[5]);

                Date now = new Date();

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));


                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
                calendar.setTime(now);
                calendar.add(Calendar.MINUTE, minutes);
                calendar.add(Calendar.HOUR_OF_DAY, hours);
                calendar.add(Calendar.DAY_OF_MONTH, days);
                calendar.add(Calendar.MONTH, months);
                Date futureTime = calendar.getTime();

                String banned_uuid = "";
                if (player != null) {
                    banned_uuid = String.valueOf(player.getUniqueId());
                } else {
                    banned_uuid = String.valueOf(offlinePlayer.getUniqueId());
                }
                String admin_uuid = senderPlayer.getUniqueId().toString();
                String reason = args[1];
                String date = format.format(now);
                String future = format.format(futureTime);
                connector.insertBanEntry(banned_uuid, admin_uuid, reason, date, future);

                if (player != null && player.isOnline()) {
                    String message = config.get("ban-message").toString();
                    message = message.replace("{0}", reason);
                    message = message.replace("{1}", date);
                    message = message.replace("{2}", future);
                    message = message.replace("{3}", senderPlayer.getName());
                    player.kickPlayer(message);
                    sender.sendMessage("§2[Info] Player " + player.getName() + " banned");
                } else {
                    sender.sendMessage("§2[Info] Player " + offlinePlayer.getName() + " banned");
                }

                return true;


            } catch (Exception e) {
                sender.sendMessage("§4[ERROR] " + e.getMessage());
                return false;
            }

        } else if (command.getName().equals("unban")) {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                String uuid = offlinePlayer.getUniqueId().toString();
                try {
                    connector.removeBanEntry(uuid);
                    sender.sendMessage("§2 Unbanned player: " + offlinePlayer.getName());
                    return true;
                } catch (Exception e) {
                    sender.sendMessage("§4[ERROR]Player not found or sth. else: " + e);
                    return false;
                }
            } catch (Exception e) {
                sender.sendMessage("§4[ERROR] " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
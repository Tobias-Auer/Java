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
    private final YamlConfiguration config;
    private final File configFile;
    private final String[] reasons;

    public EasyBanCommandListener(YamlConfiguration config, File configFile, EasyBan easyBan) {
        this.config = config;
        this.configFile = configFile;
        this.reasons = new String[]{"Hacking", "Griefing", "Spam", "Advertisement", "Insult", "Disrespect", "Money trading", "Misinformation", "Other"};
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
                ArrayList<String> bannedUuids = new ArrayList<>(config.getKeys(false));
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
            getLogger().info("Unban autocomplete started...");
            getLogger().info(command.getName());
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("admin.ban")) {
                    return new ArrayList<>();
                }
            }
            getLogger().info("Perm check passed");
            if (args.length == 1) {
                getLogger().info("Args passed");
                Set<String> playerUuids = config.getKeys(false);
                getLogger().info("get configs");
                for (String playerUuid : playerUuids) {
                    getLogger().info("outher loop");
                    if (Util.isValidUUID(playerUuid)) {
                        getLogger().info("valid uuid");
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUuid));
                        getLogger().info("got offline player");
                        completions.add(offlinePlayer.getName());
                        getLogger().info("added autocomplete");
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
                ArrayList<String> list = new ArrayList<>();
                Player player = getPlayer(args[0]);
                OfflinePlayer offlinePlayer = null;
                if (player == null) {
                    offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                }
                Player senderPlayer = getPlayer(sender.getName());
                String reason = args[1];
                int minutes = Integer.parseInt(args[2]);
                int hours = Integer.parseInt(args[3]);
                int days = Integer.parseInt(args[4]);
                int months = Integer.parseInt(args[5]);

                Date now = new Date();

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
                String date = format.format(now);

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
                calendar.setTime(now);
                calendar.add(Calendar.MINUTE, minutes);
                calendar.add(Calendar.HOUR_OF_DAY, hours);
                calendar.add(Calendar.DAY_OF_MONTH, days);
                calendar.add(Calendar.MONTH, months);
                Date futureTime = calendar.getTime();
                String future = format.format(futureTime);

                list.add(senderPlayer.getUniqueId().toString());
                list.add(reason);
                list.add(date);
                list.add(future);

                if (player != null) {
                    config.set(String.valueOf(player.getUniqueId()), list);
                } else {
                    config.set(String.valueOf(offlinePlayer.getUniqueId()), list);
                }

                config.save(configFile);
                if (player != null && player.isOnline()) {
                    String message = config.get("ban-message").toString();
                    message = message.replace("{0}", reason);
                    message = message.replace("{1}", date);
                    message = message.replace("{2}", future);
                    message = message.replace("{3}", senderPlayer.getName());
                    player.kickPlayer(message);
                    getLogger().info("§2[Info] Player + " + player.getName() + "banned");
                } else {
                    getLogger().info("§2[Info] Player + " + offlinePlayer.getName() + "banned");
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
                if (config.getString(uuid) != null) {
                    ArrayList<String> data = (ArrayList<String>) config.getStringList(uuid);
                    data.set(3, data.get(2));
                    config.set(uuid, data);
                    sender.sendMessage("§2 Unbanned player: " + offlinePlayer.getName());
                    return true;
                }
                sender.sendMessage("§4[ERROR]Player not found");
                return false;
            } catch (Exception e) {
                sender.sendMessage("§4[ERROR] " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
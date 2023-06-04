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

public class EasyBanCommandListener implements TabCompleter, CommandExecutor {
    private final YamlConfiguration config;
    private final File configFile;
    private final String[] reasons;

    public EasyBanCommandListener(YamlConfiguration config, File configFile, EasyBan easyBan) {
        this.config = config;
        this.configFile = configFile;
        this.reasons = new String[]{
                "Hacking",
                "Griefing",
                "Spam",
                "Advertisement",
                "Insult",
                "Disrespect",
                "Money trading",
                "Misinformation",
                "Other"
        };
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
                for (String playerName : players.values()) {
                    completions.add(String.valueOf(playerName));
                }
            }
            if (args.length == 2) {
                completions.addAll(List.of(reasons));
            }
            if (args.length == 3) {
                completions.add("hours");
            }
            if (args.length == 4) {
                completions.add("days");
            }
            if (args.length == 5) {
                completions.add("months");
            }
        }
        return completions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("ban")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("admin.ban")) {
                    sender.sendMessage("§4[ERROR]No permission");
                    return true;
                }
            }
            try {
                ArrayList<String> list = new ArrayList<>();
                Player player = Bukkit.getPlayer(args[0]);
                OfflinePlayer offlinePlayer = null;
                if (player == null) {
                    offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                }
                Player senderPlayer = Bukkit.getPlayer(sender.getName());
                String reason = args[1];
                int hours = Integer.parseInt(args[2]);
                int days = Integer.parseInt(args[3]);
                int months = Integer.parseInt(args[4]);

                Date now = new Date();

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
                String date = format.format(now);

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
                calendar.setTime(now);
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
                }
                return true;


            } catch (Exception e) {
                sender.sendMessage("§4[ERROR] " + e.getMessage());
                return false;
            }

        }
        return true;
    }
}

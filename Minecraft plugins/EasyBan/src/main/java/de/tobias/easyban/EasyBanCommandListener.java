package de.tobias.easyban;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EasyBanCommandListener implements TabCompleter, CommandExecutor {
    private final YamlConfiguration config;
    private final File configFile;
    private final String[] reasons;

    public EasyBanCommandListener(YamlConfiguration config, File configFile, EasyBan easyBan) {
        this.config = config;
        this.configFile = configFile;
        this.reasons = new String[] {
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
                    sender.sendMessage("§4[ERROR] Keine Berechtigung!");
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
        sender.sendMessage("HI");
        return true;
    }
}


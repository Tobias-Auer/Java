package de.tobias.easyshowmessage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EasyShowMessageCommandListener implements TabCompleter, CommandExecutor {
    private final YamlConfiguration config;
    private final File configFile;
    private final EasyShowMessage plugin;
    private final Util util;

    public EasyShowMessageCommandListener(YamlConfiguration config, File configFile, EasyShowMessage plugin) {
        this.config = config;
        this.configFile = configFile;
        this.plugin = plugin;
        this.util = new Util(config, configFile, plugin);
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("admin.show")) {
                sender.sendMessage("§4[ERROR]No permission");
                return true;
            }

            if (command.getName().equals("show")) {
                sender.sendMessage("Hello World");
                ItemStack book = util.buildAllBooks();
                player.openBook(book);
                List<String> seen = config.getStringList("rule.seen");
                seen.add(player.getName());
                config.set("rule.seen", seen);
                try {
                    config.save(configFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }

}


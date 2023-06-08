package de.tobias.easyshowmessage;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EasyShowMessageCommandListener implements TabCompleter, CommandExecutor {
    private final YamlConfiguration config;
    private final File configFile;
    private final EasyShowMessage plugin;

    public EasyShowMessageCommandListener(YamlConfiguration config, File configFile, EasyShowMessage plugin) {
        this.config = config;
        this.configFile = configFile;
        this.plugin = plugin;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equals("leave")) {
                String argumentString = String.join(" ", args);
                List<String> seen = config.getStringList("rule.seen");
                seen.remove(player.getUniqueId().toString());
                config.set("rule.seen", seen);
                try {
                    config.save(configFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                player.kickPlayer("§4" + argumentString);
                return true;
            }
            if (command.getName().equals("show")) {

            }
        }
        return true;
    }

}


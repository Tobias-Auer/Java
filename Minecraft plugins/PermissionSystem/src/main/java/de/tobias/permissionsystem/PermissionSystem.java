package de.tobias.permissionsystem;

import de.tobias.permissionsystem.listeners.AttachmentManager;
import de.tobias.permissionsystem.listeners.PermissonSystemListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PermissionSystem extends JavaPlugin {

    private AttachmentManager attachmentManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        this.attachmentManager = new AttachmentManager();

        PluginManager pluginManager = Bukkit.getPluginManager();
        PermissonSystemListener listener = new PermissonSystemListener(config, configFile, attachmentManager, this);
        pluginManager.registerEvents(listener, this);
        getCommand("permission").setExecutor(new PermissonSystemMain(config, configFile, attachmentManager, this));
    }


    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            attachmentManager.removeAttachment(player);
        }
    }
}

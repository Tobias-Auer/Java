package de.tobias.permissionsystem;

import de.tobias.permissionsystem.listeners.AttachmentManager;
import de.tobias.permissionsystem.listeners.PermissonSystemListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
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
        getCommand("permission").setExecutor(new PermissionSystemCommandListener(config, configFile, attachmentManager, this));

        for (String permission : config.getStringList("permissions")) {
            Permission permissionObj = new Permission(permission);
            Bukkit.getServer().getPluginManager().addPermission(permissionObj);
            getLogger().info("Permission added: " + permission);
        }
    }


    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            attachmentManager.removeAttachment(player);
        }
    }
}

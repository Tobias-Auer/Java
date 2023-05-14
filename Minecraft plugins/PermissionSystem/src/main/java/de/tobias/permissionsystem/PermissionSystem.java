package de.tobias.permissionsystem;

import de.tobias.permissionsystem.listeners.PermissonSystemListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PermissionSystem extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PermissonSystemListener(config, configFile), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

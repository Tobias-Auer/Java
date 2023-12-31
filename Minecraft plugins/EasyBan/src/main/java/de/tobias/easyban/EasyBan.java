package de.tobias.easyban;

import de.tobias.easyban.listener.EasyBanListener;
import de.tobias.easyban.SQLiteConnector;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class EasyBan extends JavaPlugin {

    private SQLiteConnector connector;
    @Override
    public void onEnable() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        connector = new SQLiteConnector();
        connector.connect("./database_webserver/banned.db");


        PluginManager pluginManager = Bukkit.getPluginManager();
        EasyBanListener listener = new EasyBanListener(connector, config, this);
        pluginManager.registerEvents(listener, this);

        getCommand("ban").setExecutor(new EasyBanCommandListener(connector, config, this));
        getCommand("unban").setExecutor(new EasyBanCommandListener(connector, config, this));
        getCommand("ban").setTabCompleter(new EasyBanCommandListener(connector, config, this));
        getCommand("unban").setTabCompleter(new EasyBanCommandListener(connector, config, this));


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

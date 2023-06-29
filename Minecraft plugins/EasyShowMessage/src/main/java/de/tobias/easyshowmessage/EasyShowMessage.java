package de.tobias.easyshowmessage;

import de.tobias.easyshowmessage.listerner.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class EasyShowMessage extends JavaPlugin {

    @Override
    public void onEnable() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(config, configFile), this);
        getCommand("leave").setExecutor(new EasyShowMessageCommandListener(config, configFile, this));


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}

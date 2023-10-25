package de.tobias.easyheaddrop;

import de.tobias.easyheaddrop.listeners.EasyHeadDropListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EasyHeadDrop extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EasyHeadDropListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

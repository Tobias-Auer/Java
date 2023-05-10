package de.tobias.spawnelytra;

import de.tobias.spawnelytra.listeners.SpawnElytraListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpawnElytra extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new SpawnElytraListener(this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

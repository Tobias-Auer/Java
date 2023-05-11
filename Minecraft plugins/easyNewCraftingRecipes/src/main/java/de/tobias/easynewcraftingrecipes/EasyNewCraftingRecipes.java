package de.tobias.easynewcraftingrecipes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class EasyNewCraftingRecipes extends JavaPlugin {
    public ItemStack light;

    public NamespacedKey nsKey;
    @Override
    public void onEnable() {
        // Plugin startup logic
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        light = new ItemStack(Material.LIGHT, 4);
        nsKey = new NamespacedKey(this, "light_source");

        ShapedRecipe recipe = new ShapedRecipe(nsKey, light);

        recipe.shape("***","*%*","***");

        recipe.setIngredient('*', Material.GLASS_PANE);
        recipe.setIngredient('%', Material.TORCH);

        getServer().addRecipe(recipe);
    }

    public class onJoin implements Listener {
        @EventHandler
        public void onJoinEvent(PlayerJoinEvent event) {
            event.getPlayer().discoverRecipe(nsKey);
        }
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

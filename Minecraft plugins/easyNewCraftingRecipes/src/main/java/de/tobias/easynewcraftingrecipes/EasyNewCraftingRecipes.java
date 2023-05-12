package de.tobias.easynewcraftingrecipes;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class EasyNewCraftingRecipes extends JavaPlugin {
    List<NamespacedKey> nsKeyList = new ArrayList<>();
    @Override
    public void onEnable() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new onJoin(), this);

        for (String key : config.getKeys(false)) {
            NamespacedKey namespacedKey = new NamespacedKey(this, key);
            try {

                getLogger().info("Adding new Crafting Recipe: " + namespacedKey);
                ItemStack itemStack = new ItemStack(Material.getMaterial((String) config.get(key + ".item")), config.getInt(key + ".count"));
                ShapedRecipe recipe = new ShapedRecipe(namespacedKey, itemStack);
                recipe.shape((String) config.get(key + ".top_row"), (String) config.get(key + ".middle_row"), (String) config.get(key + ".bottom_row"));
                ConfigurationSection legendSection = config.getConfigurationSection(key + ".legend");
                List<String> keyList = new ArrayList<>();
                keyList.addAll(legendSection.getKeys(false));
                for (String legendKey : keyList) {
                    char keyChars = legendKey.charAt(0);
                    Material value = Material.getMaterial((String) legendSection.get(legendKey));
                    recipe.setIngredient(keyChars, value);
                }
                getServer().addRecipe(recipe);

                nsKeyList.add(namespacedKey);
            } catch (Exception e) {
                getLogger().info("Error in Crafting Recipe: " + namespacedKey);
            }
        }
    }

    public class onJoin implements Listener {
        @EventHandler
        public void onJoinEvent(PlayerJoinEvent event) {
            for (NamespacedKey key: nsKeyList) {
                event.getPlayer().discoverRecipe(key);
            }
        }
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

package de.tobias.easynewcraftingrecipes;

import org.bukkit.configuration.ConfigurationSection;
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
import java.util.ArrayList;
import java.util.List;

public final class EasyNewCraftingRecipes extends JavaPlugin {
//    public ItemStack light;
    List<NamespacedKey> nsKeyList = new ArrayList<>();
    List<String> nsKeyListString = new ArrayList<>();
    List<ItemStack> itemStackList = new ArrayList<>();
    List<ShapedRecipe> shapedRecipeList = new ArrayList<>();
    @Override
    public void onEnable() {
        // Plugin startup logic
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection rootSection = config.getConfigurationSection("");

        if (rootSection != null) {
            for (String key : rootSection.getKeys(false)) {
                NamespacedKey namespacedKey = new NamespacedKey(this, key);
//                String otherKeys = rootSection.getKeys(true).toString();
                ItemStack itemStack = new ItemStack((Material) config.get(key+".item"), (Integer) config.get(key+".count"));
                ShapedRecipe recipe = new ShapedRecipe(namespacedKey, itemStack);
                recipe.shape((String) config.get(key+".top_row"), (String) config.get(key+".middle_row"), (String) config.get(key+".bottom_row"));

// TODO: implement setIngedient functionality!!
                
                nsKeyList.add(namespacedKey);
                nsKeyListString.add(key);
                itemStackList.add(itemStack);
            }
        }

//        recipe.setIngredient('*', Material.GLASS_PANE);
//        recipe.setIngredient('%', Material.TORCH);
//        getServer().addRecipe(recipe);
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

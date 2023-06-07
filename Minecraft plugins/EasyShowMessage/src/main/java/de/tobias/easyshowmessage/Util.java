package de.tobias.easyshowmessage;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import java.util.regex.*;
import java.io.File;
import java.util.ArrayList;

public class Util {
    private final YamlConfiguration config;
    private final File configFile;
    private final EasyShowMessage plugin;

    public Util(YamlConfiguration config, File configFile, EasyShowMessage plugin) {
        this.config = config;
        this.configFile = configFile;
        this.plugin = plugin;
    }


    public ItemStack buildAllBooks() {
        ItemStack bookItem = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();

        ArrayList<String> listOfMessages = new ArrayList<String>();
        String originalText;
        String pattern = "\\[[^\\[\\]]+,[^\\[\\]]+\\]";

        StringBuffer replacedText = new StringBuffer();
        for (String key : config.getKeys(false)) {
            if (!key.startsWith("_")) {
                originalText = config.getString(key + ".message");

                replaceWithLink(originalText, pattern);
            }
        }

        bookItem.setItemMeta(bookMeta);
        return bookItem;
    }

    private static String replaceWithLink(String input, String pattern) {
        StringBuffer replacedText = new StringBuffer();
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);

        while (matcher.find()) {
            matcher.appendReplacement(replacedText, "");
            BaseComponent[] replacement = buildLink(matcher.group(1), matcher.group(2));
            appendComponent(replacedText, replacement);
        }

        matcher.appendTail(replacedText);
        return replacedText.toString();
    }

    private static BaseComponent[] buildLink(String text, String url) {
        BaseComponent[] page = new ComponentBuilder(text)
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Go to " + url + "!").create()))
                .create();
        return page;
    }

    private static void appendComponent(StringBuffer buffer, BaseComponent[] components) {
        for (BaseComponent component : components) {
            buffer.append(component.toLegacyText());
        }
    }
}

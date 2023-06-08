package de.tobias.easyshowmessage.listerner;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JoinListener implements Listener {
    private final YamlConfiguration config;
    private final File configFile;

    public JoinListener(YamlConfiguration config, File configFile) {
        this.config = config;
        this.configFile = configFile;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (config.getStringList("rule.seen").contains(event.getPlayer().getUniqueId().toString())) {
            return;
        }
        Player player = event.getPlayer();
        TextComponent tc1 = new TextComponent();
        tc1.setText("§2Wilkommen auf meinem Server!§r\n\n\nBitte akzeptiere die ");
        TextComponent tc1_2 = new TextComponent();
        tc1_2.setText("§8[§6§nRegeln§r§8]§r!\n");
        tc1_2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.com/channels/1073289551222669443/1073289974335680522"));
        tc1_2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Regeln").create()));

        TextComponent tc2 = new TextComponent();
        tc2.setText("§8 ↳[§4§nREGELN ABLEHENEN§r§8]\n");
        tc2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/leave Du hast die Regeln nicht akzeptiert!"));
        tc2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§4Regeln nicht akzeptieren!").create()));
        TextComponent tc3 = new TextComponent();
        tc3.setText("\n\n");
        TextComponent tc4 = new TextComponent();
        tc4.setText("§8[§5§nDiscord§r§8]");
        tc4.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/vJYNnsQwf8"));
        tc4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Discord").create()));

        TextComponent tc45 = new TextComponent();
        tc45.setText("←-→");

        TextComponent tc5 = new TextComponent();
        tc5.setText("§8[§9§nWebsite§r§8]");
        tc5.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://survival0.duckdns.org/"));
        tc5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Website").create()));
        TextComponent tc6 = new TextComponent();
        tc6.setText("\n\n\n\n§cViel Spaß beim Spielen!");

        BaseComponent[] pageComponents = {
                tc1, tc1_2, tc2, tc3, tc4, tc45, tc5, tc6
        };

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.spigot().setPages(pageComponents);
        bookMeta.setTitle("Mein Server Buch");
        bookMeta.setAuthor("Dein Name");
        book.setItemMeta(bookMeta);

        player.openBook(book);

        List<String> seen = config.getStringList("rule.seen");
        seen.add(player.getUniqueId().toString());
        config.set("rule.seen", seen);
        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

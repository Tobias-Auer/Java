package de.tobias.easyheaddrop.listeners;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class EasyHeadDropListener implements Listener {
    private static String[] getStrings(Player player, String currentDate, String weapon) {
        String killerName = "";
        EntityDamageEvent damageEvent = player.getLastDamageCause();
        if (damageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageEvent = (EntityDamageByEntityEvent) damageEvent;
            if (entityDamageEvent.getDamager() instanceof Player) {
                Player killer = (Player) entityDamageEvent.getDamager();
                killerName = killer.getName();
            }
        }

        return new String[]{"§eDied on: §f" + currentDate, "§eKilled by: §f§l" + killerName, "§eKilled with: §f§l" + weapon};
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        String weapon;
        try {
            assert killer != null;
            ItemStack murderWeapon = killer.getInventory().getItemInMainHand();
            String murderWeaponName;
            if (murderWeapon.getType() == Material.AIR) {
                murderWeaponName = "Fist";
            } else if (!murderWeapon.hasItemMeta() || murderWeapon.getItemMeta().getDisplayName().equals(""))
                murderWeaponName = capitalizeMultiWordMaterialString(murderWeapon.getType().toString().toLowerCase());
            else murderWeaponName = murderWeapon.getItemMeta().getDisplayName();
            weapon = murderWeaponName;
        } catch (Exception e) {
            weapon = "-";
        }
        // Hier kannst du die Bedingungen für den Spieler einstellen, dessen Drop du bearbeiten möchtest
        if (killer != null) {
            // Erstelle den Spielerkopf
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            assert skullMeta != null;
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getName()));

            // Setze den Itemnamen
            skullMeta.setDisplayName("§a" + player.getName() + "'s §7Head");

            // Setze die Lores
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
            String currentDate = dateFormat.format(new Date());

            String[] lores = getStrings(player, currentDate, weapon);
            skullMeta.setLore(Arrays.asList(lores));

            playerHead.setItemMeta(skullMeta);

            // Füge den Spielerkopf zum Drop hinzu
            event.getDrops().add(playerHead);
        }
    }

    private String capitalizeMultiWordMaterialString(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] tokens = string.split("_");
        for (int i = 0; i < tokens.length; ++i)
            stringBuilder.append(WordUtils.capitalize(tokens[i]) + (i == (tokens.length - 1) ? "" : " "));
        return stringBuilder.toString();
    }
}




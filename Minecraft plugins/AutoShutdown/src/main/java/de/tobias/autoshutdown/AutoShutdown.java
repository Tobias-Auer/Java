package de.tobias.autoshutdown;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class AutoShutdown extends JavaPlugin {

    private boolean shutdownExecuted = false;
    private YamlConfiguration config;
    private Integer taskId;
    @Override
    public void onEnable() {
        // Plugin startup logic
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        this.config = YamlConfiguration.loadConfiguration(configFile);

    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("This needs to be executed by a player");
                return true;
            }
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("shutdown")) {
                Bukkit.broadcastMessage(Objects.requireNonNull(config.getString("warnMessage")));
                shutdownTimer(20);
                return true;
            }
            return false;
        }

    private void shutdownTimer(int delayInTicks) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
//        int delayTicks = 5 * 60 * 20; // 5 Minuten Verzögerung (1 Minute = 60 Sekunden, 1 Sekunde = 20 Ticks)
        taskId = scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                // Hier wird deine Funktion nach der Verzögerung von 5 Minuten ausgeführt
                doShutdown();
            }
        }, delayInTicks);
    }

    private void doShutdown() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        int delayTicks = 10 * 20; // 10 Sekunden Verzögerung (1 Sekunde = 20 Ticks)

        // Nachrichten im Chat anzeigen alle 1 Sekunde
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            int counter = 10; // Anzahl der Nachrichten

            @Override
            public void run() {
                if (counter > 0) {
                    Bukkit.broadcastMessage("§4"+counter + " §bSekunden verbleibend!");
                    counter--;
                } else {
                    // Hier wird die Hauptfunktion nach 10 Sekunden Verzögerung aufgerufen
                    if(!shutdownExecuted){
                        shutdownExecuted = true;
                        for (World welt : Bukkit.getWorlds()) {
                            welt.save();
                            getLogger().info("Welt erfolgreich gespeichert: " + welt.getName());
                        }

                        try {
                            String command = "docker stop <container_name>";
                            Process process = Runtime.getRuntime().exec(command);
                            process.waitFor();
                            int exitCode = process.exitValue();
                            if (exitCode == 0) {
                                getLogger().info("Docker container stopped successfully.");
                            } else {
                                getLogger().warning("Failed to stop Docker container.");
                            }
                        } catch (IOException | InterruptedException e) {
                            getLogger().severe("An error occurred while stopping Docker container: " + e.getMessage());
                        }
                    }

                }
            }
        }, 0, 20); // Start sofort, wiederholen alle 1 Sekunde (20 Ticks)
    }

    @Override
    public void onDisable() {
        // Timer abbrechen, wenn das Plugin deaktiviert wird
        Bukkit.getScheduler().cancelTask(taskId);
    }
}

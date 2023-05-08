package de.tobias.spawnprotect;

import de.tobias.spawnprotect.listeners.protectSpawn;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public final class SpawnProtect extends JavaPlugin {
    private BukkitRunnable particleRectangleTask;
    private int ax;
    private int bx;
    private int ay;
    private int by;
    private int az;
    private int bz;
    private boolean debugEnabled;

    @Override
    public void onEnable() {
        // Load the configuration file
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        this.debugEnabled = config.getBoolean("debugEnabled");

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new protectSpawn(config, debugEnabled), this);

        this.ax = (int) config.get("ax");
        getLogger().info(String.valueOf(ax));
        this.bx = (int) config.get("bx");
        this.ay = (int) config.get("ay");
        this.by = (int) config.get("by");
        this.az = (int) config.get("az");
        this.bz = (int) config.get("bz");


        // Partikeltyp und Größe
        Particle particleType = Particle.NOTE;
        float particleSize = 10000.0f;
        int updateInterval = 10;
        // RGB-Farbwerte für den Redstone-Effekt
        float red = 1.0f;    // Rot-Komponente (0.0 - 1.0)
        float green = 0.0f;  // Grün-Komponente (0.0 - 1.0)
        float blue = 0.0f;   // Blau-Komponente (0.0 - 1.0)
        float alpha = 1.0f;  // Transparenz (0.0 - 1.0)

        // Hohles Redstone-Rechteck starten


        if (debugEnabled) {
            startHollowParticleRectangle(ax, bx, ay, by, az, bz, particleType, particleSize, updateInterval);
        }
    }

    private void startHollowParticleRectangle(int ax, int bx, int ay, int by, int az, int bz, Particle particleType, float particleSize, int updateInterval) {
        particleRectangleTask = new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("world"); // Ändern Sie "world" entsprechend dem Namen Ihrer Welt

                // Vertikale Kanten des Rahmens
                for (int y = Math.min(ay, by); y <= Math.max(ay, by); y++) {
                    Location location1 = new Location(world, ax, y, az);
                    Location location2 = new Location(world, ax, y, bz);
                    Location location3 = new Location(world, bx, y, az);
                    Location location4 = new Location(world, bx, y, bz);

                    assert world != null;
                    world.spawnParticle(particleType, location1, 1, 0, 0, 0, particleSize);
                    world.spawnParticle(particleType, location2, 1, 0, 0, 0, particleSize);
                    world.spawnParticle(particleType, location3, 1, 0, 0, 0, particleSize);
                    world.spawnParticle(particleType, location4, 1, 0, 0, 0, particleSize);
                }

                // Horizontale Kanten des Rahmens
                for (int x = Math.min(ax, bx); x <= Math.max(ax, bx); x++) {
                    Location location1 = new Location(world, x, ay, az);
                    Location location2 = new Location(world, x, ay, bz);
                    Location location3 = new Location(world, x, by, az);
                    Location location4 = new Location(world, x, by, bz);

                    world.spawnParticle(particleType, location1, 1, 0, 0, 0, particleSize);
                    world.spawnParticle(particleType, location2, 1, 0, 0, 0, particleSize);
                    world.spawnParticle(particleType, location3, 1, 0, 0, 0, particleSize);
                    world.spawnParticle(particleType, location4, 1, 0, 0, 0, particleSize);
                } // Eckpunkte des Rahmens
                Location location1 = new Location(world, ax, ay, az);
                Location location2 = new Location(world, ax, ay, bz);
                Location location3 = new Location(world, ax, by, az);
                Location location4 = new Location(world, ax, by, bz);
                Location location5 = new Location(world, bx, ay, az);
                Location location6 = new Location(world, bx, ay, bz);
                Location location7 = new Location(world, bx, by, az);
                Location location8 = new Location(world, bx, by, bz);

                world.spawnParticle(particleType, location1, 1, 0, 0, 0, particleSize);
                world.spawnParticle(particleType, location2, 1, 0, 0, 0, particleSize);
                world.spawnParticle(particleType, location3, 1, 0, 0, 0, particleSize);
                world.spawnParticle(particleType, location4, 1, 0, 0, 0, particleSize);
                world.spawnParticle(particleType, location5, 1, 0, 0, 0, particleSize);
                world.spawnParticle(particleType, location6, 1, 0, 0, 0, particleSize);
                world.spawnParticle(particleType, location7, 1, 0, 0, 0, particleSize);
                world.spawnParticle(particleType, location8, 1, 0, 0, 0, particleSize);
            }
        };

        particleRectangleTask.runTaskTimer(this, 0, updateInterval);
    }

    @Override
    public void onDisable() {
        if (particleRectangleTask != null) {
            particleRectangleTask.cancel();
        }
    }
}

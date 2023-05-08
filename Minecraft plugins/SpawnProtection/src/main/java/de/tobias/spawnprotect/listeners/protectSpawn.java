package de.tobias.spawnprotect.listeners;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import static org.bukkit.Bukkit.getLogger;


public class protectSpawn implements Listener {


    private final int ax;
    private final int bx;
    private final int ay;
    private final int by;
    private final int az;
    private final int bz;
    private final boolean debugEnabled;

    public protectSpawn(YamlConfiguration config, boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        this.ax = (int) config.get("ax");
        this.bx = (int) config.get("bx");
        this.ay = (int) config.get("ay");
        this.by = (int) config.get("by");
        this.az = (int) config.get("az");
        this.bz = (int) config.get("bz");

    }


    private boolean checkSpawn(Entity entity) {
        int px = entity.getLocation().getBlockX();
        int py = entity.getLocation().getBlockY();
        int pz = entity.getLocation().getBlockZ();

        boolean inSpawn = (px >= ax && px <= bx) &&
                (py >= ay && py <= by) &&
                (pz >= az && pz <= bz);
        if (entity instanceof Player && debugEnabled){
            getLogger().info("px:" + px + ", py:" + py + ", pz:" + pz);
            getLogger().info("ax: " + ax + ", ay: " + ay + ", az: " + az);
            getLogger().info("bx: " + bx + ", by: " + by + ", bz: " + bz);

            getLogger().info(String.valueOf(inSpawn));
        }

        return inSpawn;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        if (checkSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSetBlock(BlockPlaceEvent event) {
        if (checkSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onDamge(EntityDamageEvent event) {
        if (checkSpawn(event.getEntity())) {

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucket(PlayerBucketEmptyEvent event) {
        if (checkSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onUnleashEntity(PlayerUnleashEntityEvent event) {
        if (checkSpawn(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (checkSpawn(event.getEntity())) {
            event.setCancelled(true);
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onExplosion(ExplosionPrimeEvent event) {
        if (checkSpawn(event.getEntity())) {
            event.setCancelled(true);
        }
    }

}

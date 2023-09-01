package de.tobias.serverconnector.listener;

import de.tobias.serverconnector.SQLiteConnector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.Bukkit.getLogger;


public class JoinListener implements Listener {

    private boolean alreadyActivated;
    private final SQLiteConnector connector;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        connector.insertData(event.getPlayer().getUniqueId()+"~online", "status", "status");
        if (!alreadyActivated) {
            connector.insertData("backup", "meta", "doAction");
            getLogger().info("Backup enabled");
            alreadyActivated = true;
        }
//        else {
//            alreadyActivated = false;
//        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        connector.insertData(event.getPlayer().getUniqueId()+"~offline", "status", "status");
    }

    public JoinListener(SQLiteConnector connector) {
        this.connector = connector;
        this.alreadyActivated = false;
    }
}

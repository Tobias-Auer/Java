package de.tobias.serverconnector.listener;

import de.tobias.serverconnector.SQLiteConnector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static org.bukkit.Bukkit.getLogger;


public class JoinListener implements Listener {

    private boolean alreadyActivated;
    private final SQLiteConnector connector;

    public JoinListener(SQLiteConnector connector) {
        this.connector = connector;
        this.alreadyActivated = false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!alreadyActivated) {
            connector.insertData("backup");
            getLogger().info("Backup enabled");
        }
        else {
            alreadyActivated = false;
        }
    }
}

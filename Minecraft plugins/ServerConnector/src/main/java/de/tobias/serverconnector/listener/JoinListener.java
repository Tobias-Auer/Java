package de.tobias.serverconnector.listener;

import de.tobias.serverconnector.SQLiteConnector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;


public class JoinListener implements Listener {

    private final SQLiteConnector prefixConnector;
    private boolean alreadyActivated;
    private final SQLiteConnector connector;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
//        String prefix = "§4[PREFIX] ";
        String prefix = prefixConnector.readPrefixData(player.getUniqueId().toString());

        player.setDisplayName(prefix + player.getName());
        player.setPlayerListName(prefix + player.getName());

        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }

        team.setPrefix(prefix);
        team.addEntry(player.getName());


        connector.insertData(event.getPlayer().getUniqueId()+"~online", "status", "status");
        if (!alreadyActivated) {
            connector.insertData("backup", "meta", "doAction");
            getLogger().info("Backup enabled");
            alreadyActivated = true;
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        connector.insertData(event.getPlayer().getUniqueId()+"~offline", "status", "status");
    }

    public JoinListener(SQLiteConnector connector, SQLiteConnector prefixConnector) {
        this.connector = connector;
        this.prefixConnector = prefixConnector;
        this.alreadyActivated = false;
    }
}

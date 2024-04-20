package de.tobias.serverconnector.listener;

import de.tobias.serverconnector.SQLiteConnector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.SQLException;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;


public class JoinListener implements Listener {

    private final SQLiteConnector prefixConnector;
    private final SQLiteConnector connector;
    private boolean alreadyActivated;

    public JoinListener(SQLiteConnector connector, SQLiteConnector prefixConnector) {
        this.connector = connector;
        this.prefixConnector = prefixConnector;
        this.alreadyActivated = false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
//        String prefix = "§4[PREFIX] ";

        prefixConnector.connect("./database_webserver/prefixes.db");
        String prefix = prefixConnector.readPrefixData(player.getUniqueId().toString()) + " ";
        prefixConnector.disconnect();

        if (Objects.equals(player.getUniqueId().toString().replace("-",""), "4ebe5f6fc23143159d60097c48cc6d30")) {
            prefix = "§2§l[Owner] " + prefix + "§2§l";
        }
        getLogger().info("Neuer prefix: ");
        getLogger().info(prefix);

        player.setDisplayName(prefix + player.getName());
        player.setPlayerListName(prefix + player.getName());

        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(player.getName());
        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
        }

        team.setPrefix(prefix);
        team.addEntry(player.getName());


        connector.insertData(event.getPlayer().getUniqueId() + "~online", "status", "status");
        if (!alreadyActivated) {
            connector.insertData("backup", "meta", "doAction");
            getLogger().info("Backup enabled");
            alreadyActivated = true;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Hier setzt du den Spieler wieder auf den Standard-Prefix oder entfernst ihn aus dem Team
        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(player.getName());
        if (team != null) {
            team.setPrefix("");  // Setze den Prefix auf den Standardwert oder ""
            team.removeEntry(player.getName());
        }

        // Setze den DisplayName und PlayerListName auf den Standardwert zurück
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        connector.insertData(event.getPlayer().getUniqueId() + "~offline", "status", "status");
    }

}

package de.tobias.easyban;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import static org.bukkit.Bukkit.getLogger;
public class SQLiteConnector {

    private Connection connection;

    public void connect(String databasePath) {
        try {
            Class.forName("org.sqlite.JDBC");
            String databaseURL = "jdbc:sqlite:" + databasePath;
            connection = DriverManager.getConnection(databaseURL);
            getLogger().info("Connected to SQLite database.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from SQLite database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertBanEntry(String uuid, String admin_uuid, String reason, String start, String end) {
        String query = "INSERT INTO main (uuid, admin, reason, start, end) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, uuid);
            statement.setString(2, admin_uuid);
            statement.setString(3, reason);
            statement.setString(4, start);
            statement.setString(5, end);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeBanEntry(String uuid) {
        String query = "DELETE FROM main WHERE uuid = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, uuid);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getAllBannedUuids() {
        ArrayList<String> bannedPlayers = new ArrayList<>();
        String query = "SELECT uuid FROM main";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                String playerName = resultSet.getString("uuid");
                bannedPlayers.add(playerName);
            }

            // Close resources (ResultSet, Statement) when done
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();  // Handle the exception according to your application's needs
        }

        return bannedPlayers;
    }



    public Map<String, String> readBanEntry(String uuid) {
        String query = "SELECT uuid, admin, reason, start, end FROM main WHERE uuid = ?";
        Map<String, String> banEntryMap = new HashMap<>();

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                banEntryMap.put("uuid", resultSet.getString("uuid"));
                banEntryMap.put("admin", resultSet.getString("admin"));
                banEntryMap.put("reason", resultSet.getString("reason"));
                banEntryMap.put("start", resultSet.getString("start"));
                banEntryMap.put("end", resultSet.getString("end"));

                getLogger().info("banread: " + banEntryMap);
            }

            // Close resources (ResultSet, Statement) when done
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return banEntryMap;
    }




}
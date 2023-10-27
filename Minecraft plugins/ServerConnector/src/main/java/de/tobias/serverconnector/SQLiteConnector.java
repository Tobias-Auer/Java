package de.tobias.serverconnector;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, score INTEGER NOT NULL)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(String data, String table, String column) {
        String query = "INSERT INTO " + table + " ("+column+") VALUES (?)";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, data);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeData(String data, String table, String column) {
        String query = "DELETE FROM " + table + " WHERE " + column + " = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, data);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void readData() {
        String query = "SELECT * FROM meta";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String action = resultSet.getString("doAction");
                getLogger().info("doAction: " + action);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String readPrefixData(String uuid) {
        uuid = uuid.replace("-", "");
        String query = "SELECT prefix FROM main WHERE uuid = ?";
        getLogger().info("readPrefixData: " + uuid);
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String prefix = resultSet.getString("prefix");
                getLogger().info("Prefix: " + prefix);
                return prefix;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ""; // Return "" if no data is found
    }

}
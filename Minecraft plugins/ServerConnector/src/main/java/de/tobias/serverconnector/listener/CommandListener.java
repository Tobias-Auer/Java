package de.tobias.serverconnector.listener;

import de.tobias.serverconnector.SQLiteConnector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor {

    private final SQLiteConnector connector;

    public CommandListener(SQLiteConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            connector.insertData("shutdown");
        }
        else {
            connector.removeData("shutdown");
        }
        sender.sendMessage("Done!");
        return false;
    }
}

package de.tobias.serverconnector.listener;

import de.tobias.serverconnector.SQLiteConnector;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static org.bukkit.Bukkit.getLogger;

public class CommandListener implements CommandExecutor {

    private final SQLiteConnector connector;
    public boolean eventCancel = false;
    public CommandListener(SQLiteConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("admin.action.shutdown")) {

            Timer timer = new Timer();
            Timer timer2 = new Timer();
            if (args.length == 0) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
                calendar.add(Calendar.SECOND, 10);

                TimerTask shutdownTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (!eventCancel) {
                            // Broadcast und Ausführung von shutdown()
                            Bukkit.broadcastMessage("Der Server wird jetzt heruntergefahren!");
                            connector.insertData("shutdown", "meta", "doAction");
                            Bukkit.shutdown();
                        }
                    }
                };

                TimerTask countdownTask = new TimerTask() {
                    int remainingSeconds = 10;
                    @Override
                    public void run() {
                        if (!eventCancel) {
                            if (remainingSeconds > 0) {
                                // Broadcast des Countdowns im Chat
                                Bukkit.broadcastMessage("§4Der Server wird in §5" + remainingSeconds + " §4Sekunden heruntergefahren!");
                                remainingSeconds--;
                            } else {
                                // Countdown abgelaufen
                                timer2.cancel();
                            }
                        }
                    }
                };

                timer.schedule(shutdownTask, calendar.getTime());
                timer2.scheduleAtFixedRate(countdownTask, 0, 1000);
            }
            else {
                getLogger().info("KILL SHUTDOWN");
                eventCancel = true;
                connector.removeData("shutdown", "meta", "doAction");
                Bukkit.broadcastMessage("Servershutdown wurde von " + sender.getName() + " abgebrochen!");
            }
            sender.sendMessage("Done!");
            return true;
        }

        return false;
    }


}

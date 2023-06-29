package de.tobias.serverconnector;

import de.tobias.serverconnector.listener.CommandListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class ServerConnector extends JavaPlugin {

    private SQLiteConnector connector;
    @Override
    public void onEnable() {


        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        getLogger().info("Aktuelle Zeit in Deutschland: " + hour + ":" + minute + ":" + second);
        connector = new SQLiteConnector();
        connector.connect("./databse_webserver/data.db");
        scheduleShutdownBroadcast();
        scheduleShutdown();

        getCommand("shutdown").setExecutor(new CommandListener(connector));
        connector.readData();
    }

    @Override
    public void onDisable() {
        connector.disconnect();
    }

    private void scheduleShutdownBroadcast() {
        getLogger().info("sheduleShutdown!");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 55);
        calendar.set(Calendar.SECOND, 0);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Broadcast, dass der Server in 5 Minuten heruntergefahren wird
                Bukkit.broadcastMessage("Der Server wird in 5 Minuten heruntergefahren!");
                Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
                calendar2.set(Calendar.HOUR_OF_DAY, 22);
                calendar2.set(Calendar.MINUTE, 0);
                calendar2.set(Calendar.SECOND, 0);
                // Starte den Countdown-Timer für 5 Minuten
                Date currentTime = new Date();
                Date scheduledTime = calendar2.getTime();

                long timeDifference = (scheduledTime.getTime() - currentTime.getTime()) / 1000;
                System.out.println("Seconds until execution: " + timeDifference);
                startCountdown((int) timeDifference);
            }
        }, calendar.getTime());
    }

    private void scheduleShutdown() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Broadcast und Ausführung von shutdown()
                Bukkit.broadcastMessage("Der Server wird jetzt heruntergefahren!");
                connector.insertData("shutdown");
                Bukkit.shutdown();
            }
        }, calendar.getTime());
    }

    private void startCountdown(int seconds) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int remainingSeconds = seconds;

            @Override
            public void run() {
                if (remainingSeconds > 0) {
                    // Broadcast des Countdowns im Chat
                    if (remainingSeconds < 10) {
                        Bukkit.broadcastMessage("§4Der Server wird in §5" + remainingSeconds + " §4Sekunden heruntergefahren!");
                    }
                    remainingSeconds--;
                } else {
                    // Countdown abgelaufen
                    timer.cancel();
                }
            }
        }, 0, 1000); // Starte den Timer sofort und wiederhole alle 1 Sekunde
    }
}

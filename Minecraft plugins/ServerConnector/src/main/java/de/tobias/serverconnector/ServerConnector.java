package de.tobias.serverconnector;

import de.tobias.serverconnector.listener.CommandListener;
import de.tobias.serverconnector.listener.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Array;
import java.util.*;

import static java.lang.String.valueOf;

public final class ServerConnector extends JavaPlugin {

    private SQLiteConnector connector;
    private SQLiteConnector prefixConnector;
    @Override
    public void onEnable() {
        scheduleShutdownBroadcast();
        scheduleShutdown();
        connector = new SQLiteConnector();
        prefixConnector = new SQLiteConnector();
        connector.connect("./database_webserver/data.db");

        getCommand("shutdown").setExecutor(new CommandListener(connector));
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(connector, prefixConnector), this);

        connector.readData();
        scheduleLogin();
    }

    @Override
    public void onDisable() {
        connector.disconnect();
    }

    private void scheduleShutdownBroadcast() {
        getLogger().info("scheduleShutdown!");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 55);
        calendar.set(Calendar.SECOND, 0);

        // Überprüfen, ob die aktuelle Uhrzeit vor 21:55 Uhr liegt
        Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        if (currentTime.before(calendar)) {
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
    }


    private void scheduleShutdown() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Überprüfen, ob die aktuelle Uhrzeit vor 22:00 Uhr liegt
        Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
        if (currentTime.before(calendar)) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Broadcast und Ausführung von shutdown()
                    Bukkit.broadcastMessage("Der Server wird jetzt heruntergefahren!");
                    connector.insertData("shutdown", "meta", "doAction");
                    Bukkit.shutdown();
                }
            }, calendar.getTime());
        }
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


    private void scheduleLogin() {
        getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                List<String> loginDataList = connector.readLoginData();

                for (String loginData : loginDataList) {
                    getLogger().info(loginData);
                    String[] dataParts = loginData.split(" ");

                    if (dataParts.length == 2) {
                        String uuidString = dataParts[0];
                        String secretPin = dataParts[1];

                        Player player = Bukkit.getPlayer(UUID.fromString(uuidString));
                        if (player != null) {
                            player.sendMessage("Dein Pin ist: " + secretPin);
                            connector.removeData(uuidString.replace("-", ""), "login", "uuid");
                        }
                    }
                }
            }
        }, 0L, 20L);
    }
}
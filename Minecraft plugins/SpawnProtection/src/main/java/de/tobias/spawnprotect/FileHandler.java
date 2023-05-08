package de.tobias.spawnprotect;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileHandler {

    private final File configFile;
    private final YamlConfiguration config;

    private int ax;
    private int ay;
    private int az;
    private int bx;
    private int by;
    private int bz;

    public FileHandler(File configFile) throws IOException {
        if (configFile == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        this.configFile = configFile;
        if (!configFile.exists()) {
            configFile.createNewFile();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadConfig();
    }

    private void loadConfig() {
        ax = config.getInt("ax");
        ay = config.getInt("ay");
        az = config.getInt("az");
        bx = config.getInt("bx");
        by = config.getInt("by");
        bz = config.getInt("bz");
    }

    public void resetConfig() throws IOException {
        config.set("bx", -76);
        config.set("ax", -116);
        config.set("ay", 167);
        config.set("by", 117);
        config.set("bz", 253);
        config.set("az", 218);
        config.save(configFile);
    }

    public int getAx() {
        return ax;
    }

    public int getAy() {
        return ay;
    }

    public int getAz() {
        return az;
    }

    public int getBx() {
        return bx;
    }

    public int getBy() {
        return by;
    }

    public int getBz() {
        return bz;
    }

    public void setAx(int ax) {
        this.ax = ax;
    }

    public void setAy(int ay) {
        this.ay = ay;
    }

    public void setAz(int az) {
        this.az = az;
    }

    public void setBx(int bx) {
        this.bx = bx;
    }

    public void setBy(int by) {
        this.by = by;
    }

    public void setBz(int bz) {
        this.bz = bz;
    }
}

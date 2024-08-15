package net.packet;

import net.GameClient;
import net.GameServer;

import java.util.Arrays;

public class Packet00Login extends Packet{

    private String username;

    public Packet00Login(byte[] data) {
        super(00);
        this.username = readData(data);
    }

    public Packet00Login(String username) {
        super(00);
        this.username = username;
    }

    @Override
    public void writeData(GameClient client) {
        client.sendData(getData());
    }

    @Override
    public void writeData(GameServer server) {
        server.sendDataToAllClients(getData());
    }

    @Override
    public byte[] getData() {
        return ("00"+ Arrays.toString(this.username.getBytes())).getBytes();
    }

    public String getUserName() {
        return username;
    }
}

package net.packet;

import net.GameClient;
import net.GameServer;

public abstract class Packet {

    public static enum PacketTypes {
        INVALID(-1), LOGIN(00), DISCONNECT(01);

        private int packetId;
        private PacketTypes(int packetId) {
            this.packetId = packetId;
        }
        public int getPacketId() {
            return packetId;
        }
    }

    public byte packetId;

    public Packet(int packetId) {
        this.packetId = (byte) packetId;
    }

    public abstract void writeData(GameClient client);

    public abstract void writeData(GameServer server);

    public String readData(byte[] data) {
        String message = new String(data).trim();
        return message.substring(2);
    }

    public abstract byte[] getData();

    public static PacketTypes lookupPacket(String id) {
        try {
            return lookupPacket(Integer.parseInt(id));
        } catch (Exception e) {
            return PacketTypes.INVALID;
        }
    }

    public static PacketTypes lookupPacket(int id) {
        for (PacketTypes type : PacketTypes.values()) {
            if (type.getPacketId() == id) {
                return type;
            }
        }
        return PacketTypes.INVALID;
    }
}

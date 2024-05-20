package com.alan.clients.util.germ;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.collection.IntObjectHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.util.HashMap;

public class GermPacketHandler {
    private static final HashMap<Integer,Class> registered = new HashMap<>();


    static {
        registerPacket(new GermConcatPacket());
        registerPacket(new GermOpenGUIPacket());
    }

    public static void registerPacket(GermPacket packet){
        registered.put(packet.getPacketId(),packet.getClass());
    }

    public static void handleSend(GermPacket packet){
        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
        packet.writePacket(buf);
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germplugin-netease",buf));
    }


    public static void handleReceive(ByteBuf buf){
        PacketBuffer packet = new PacketBuffer(buf);
        int id = packet.readInt();
        try {
            Class c = registered.get(id);
            if(c!= null){
                GermPacket germPacket = (GermPacket) c.newInstance();
                germPacket.readPacket(packet);
                germPacket.handle();
            }

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

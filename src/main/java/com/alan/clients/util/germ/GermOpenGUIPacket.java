package com.alan.clients.util.germ;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class GermOpenGUIPacket implements GermPacket {
    private String type;

    private String name;

    private String yml;

    @Override
    public int getPacketId() {
        return 73;
    }

    @Override
    public void readPacket(PacketBuffer buf) {
        this.type = buf.readStringFromBuffer(32767);
        this.name = buf.readStringFromBuffer(32767);
        this.yml = buf.readStringFromBuffer(9999999);
    }


    @Override
    public void writePacket(PacketBuffer buf) {

    }

    @Override
    public void handle() {

        if(type.equalsIgnoreCase("gui")){
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer()
                    .writeInt(4)
                    .writeInt(0)
                    .writeInt(0))
                    .writeString(name)
                    .writeString(name)
                    .writeString(name)));

            if(name.equalsIgnoreCase("mainmenu")){
                System.out.println(332);

                PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                buf.writeInt(13);
                buf.writeString("mainmenu").writeString("自适应背景$主分类$subject_bedwar").writeInt(0);
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease",buf));


                buf = new PacketBuffer(Unpooled.buffer());
                buf.writeInt(13);
                buf.writeString("mainmenu").writeString("自适应背景$细分分类$游戏3").writeInt(0);
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease",buf));

                buf = new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47, 51, 34, 123, 34, 101, 110, 116, 114, 121, 34, 58, 51, 44, 34, 115, 105, 100, 34, 58, 34, 66, 69, 68, 87, 65, 82, 47, 98, 119, 45, 116, 101, 97, 109, 34, 125}));
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease",buf));

            }

        }
    }
}

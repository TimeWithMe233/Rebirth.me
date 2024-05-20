package com.alan.clients.util.germ;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;

public class GermConcatPacket implements GermPacket {
    private boolean isEnd;


    private static int start = 0;
    private static byte[] concatData = null;

    private byte[] data;

    private int length;

    private boolean isFirst;

    @Override
    public int getPacketId() {
        return -1;
    }

    @Override
    public void readPacket(PacketBuffer buf) {
        this.isFirst = buf.readBoolean();
        this.length = buf.readInt();
        this.isEnd = buf.readBoolean();
        this.data = buf.readByteArray();
    }


    @Override
    public void writePacket(PacketBuffer buf) {

    }

    @Override
    public void handle() {
        if(isFirst){
            concatData = new byte[length];
        }
        System.arraycopy(this.data,0,concatData,start,this.data.length);
        start+= this.data.length;

        if(this.isEnd){
            ByteBuf buf = Unpooled.wrappedBuffer(concatData);
            GermPacketHandler.handleReceive(buf);
            start = 0;
            concatData = null;
        }
    }
}

package com.alan.clients.util.germ;

import net.minecraft.network.PacketBuffer;

public interface GermPacket {

    int getPacketId();

    void readPacket(PacketBuffer buf);

    void writePacket(PacketBuffer buf);

    void handle();
}

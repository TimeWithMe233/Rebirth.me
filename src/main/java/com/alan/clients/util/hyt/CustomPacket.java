/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package com.alan.clients.util.hyt;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public interface CustomPacket {
    Minecraft mc = Minecraft.getMinecraft();

    String getChannel();

    void process(ByteBuf var1);
}


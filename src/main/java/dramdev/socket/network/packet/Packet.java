package dramdev.socket.network.packet;

import dramdev.socket.network.buffer.PacketBuffer;
import dramdev.socket.network.handler.ClientHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author DiaoLing
 * @since 4/7/2024
 */

public abstract class Packet {
    public abstract void encode(PacketBuffer buf);

    public abstract void decode(PacketBuffer buf);

    public abstract void handler(ChannelHandlerContext ctx, ClientHandler handler);
}
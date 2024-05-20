package dramdev.socket.network.client;

import dramdev.socket.network.handler.ClientHandler;
import dramdev.socket.network.handler.PacketDecoder;
import dramdev.socket.network.handler.PacketEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author DiaoLing
 * @since 4/7/2024
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));

        ch.pipeline().addLast(new LengthFieldPrepender(4));

        ch.pipeline().addLast(new PacketDecoder());
        ch.pipeline().addLast(new PacketEncoder());
        ch.pipeline().addLast(new ClientHandler());
    }
}
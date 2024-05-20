package dramdev.socket.network.handler;

import com.alan.clients.Client;
import com.alan.clients.util.web.Browser;
import dramdev.socket.network.packet.Packet;
import dramdev.socket.network.packet.impl.info.UserInfoPacket;
import dramdev.socket.enums.ClientType;
import dramdev.socket.enums.Rank;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;

import java.net.SocketException;

/**
 * @author DiaoLing
 * @since 4/7/2024
 */
public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        LogManager.getLogger().info("Received packet: " + packet.getClass().getSimpleName());

        packet.handler(ctx, this);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LogManager.getLogger().info("Connected to server: " + ctx.channel().remoteAddress());
        if(Client.name.equals("DreamDev")) {
            ctx.writeAndFlush(new UserInfoPacket(
                    ClientType.Rebirth,
                    0,
                    EnumChatFormatting.LIGHT_PURPLE + "[" + Client.location2 + "] " + EnumChatFormatting.RESET + Client.name,
                    Rank.OWNER,
                    0,
                    114514
            ));}
            else {
                ctx.writeAndFlush(new UserInfoPacket(
                        ClientType.Rebirth,
                        0,
                        EnumChatFormatting.LIGHT_PURPLE + "[" + Client.location2 + "] " + EnumChatFormatting.RESET + Client.name,
                        Rank.USER,
                        0,
                        114514
                ));
            }
        }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LogManager.getLogger().warn("Disconnected from server.");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException) {
            LogManager.getLogger().error("Connection reset by peer or server shutdown.");
        } else {
            cause.printStackTrace();
        }
        ctx.close();
    }
}
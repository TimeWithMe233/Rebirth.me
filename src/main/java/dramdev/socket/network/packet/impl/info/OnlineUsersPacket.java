package dramdev.socket.network.packet.impl.info;

import dramdev.socket.network.buffer.PacketBuffer;
import dramdev.socket.network.handler.ClientHandler;
import dramdev.socket.network.info.record.OnlineUserInfo;
import dramdev.socket.network.packet.Packet;
import dramdev.socket.network.user.UserManager;
import dramdev.socket.enums.ClientType;
import dramdev.socket.enums.Rank;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @author DiaoLing
 * @since 4/8/2024
 */

public class OnlineUsersPacket extends Packet {
    private List<OnlineUserInfo> onlineUsers;

    public OnlineUsersPacket() {
    }

    public OnlineUsersPacket(List<OnlineUserInfo> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeList(onlineUsers, (buffer, user) -> {
            buffer.writeEnum(user.getClient());
            buffer.writeString(user.getUsername());
            buffer.writeString(user.getInGameName());
            buffer.writeEnum(user.getRank());
        });
    }

    @Override
    public void decode(PacketBuffer buf) {
        this.onlineUsers = buf.readList(buffer -> new OnlineUserInfo(
                buffer.readEnum(ClientType.class),
                buffer.readString(),
                buffer.readString(),
                buffer.readEnum(Rank.class)
        ));
    }

    @Override
    public void handler(ChannelHandlerContext ctx, ClientHandler handler) {
        UserManager.setOnlineUsers(getOnlineUsers());
    }

    public List<OnlineUserInfo> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(List<OnlineUserInfo> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }
}
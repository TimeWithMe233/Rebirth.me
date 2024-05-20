package dramdev.socket.network;

import dramdev.socket.network.client.SocketClient;
import dramdev.socket.network.packet.Packet;
import dramdev.socket.network.packet.impl.message.ChatMessagePacket;
import dramdev.socket.network.packet.impl.operation.OperationPacket;
import dramdev.socket.network.user.UserManager;
import dramdev.socket.enums.ChannelType;
import dramdev.socket.enums.Operation;

/**
 * @author DiaoLing
 * @since 4/8/2024
 */
public class SocketManager {
    private final SocketClient client = new SocketClient();

    private static String prefix = "!";

    public SocketClient getClient() {
        return client;
    }

    public String getPrefix() {
        return prefix;
    }

    public void send(Packet packet) {
        client.send(packet);
    }

    // 我去发你
    public void chat(String message) {
        this.send(new ChatMessagePacket(
                ChannelType.GLOBAL,
                message,
                System.currentTimeMillis()));
    }

    public void operation(Operation operation, String targetUsername, String message) {
        this.send(new OperationPacket(
                UserManager.getUser().getUsername(),
                targetUsername,
                message,
                operation
        ));
    }
}

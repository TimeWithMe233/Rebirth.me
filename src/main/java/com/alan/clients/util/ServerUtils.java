/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package com.alan.clients.util;

import net.minecraft.client.multiplayer.ServerData;

import static com.alan.clients.util.interfaces.InstanceAccess.mc;

public final class ServerUtils {

    public static String getRemoteIp() {
        String serverIp = "Singleplayer";

        if (mc.theWorld.isRemote) {
            final ServerData serverData = mc.getCurrentServerData();
            if(serverData != null)
                serverIp = serverData.serverIP;
        }

        return serverIp;
    }
}
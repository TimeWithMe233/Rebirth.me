package com.alan.clients.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;


public class Wrapper {



	public static Minecraft getMinecraft() {
		return Minecraft.getMinecraft();
	}

	public static ItemRenderer getItemRenderer() {
		return getMinecraft().getItemRenderer();
	}



	public static EntityPlayerSP getPlayer() {
		return getMinecraft().thePlayer;
	}

	public static WorldRenderer getWorldRenderer() {
		return Tessellator.getInstance().getWorldRenderer();
	}

	public static WorldClient getWorld() {
		return getMinecraft().theWorld;
	}

	public static RenderItem getRenderItem() {
		return getMinecraft().getRenderItem();
	}

	public static PlayerControllerMP getPlayerController() {
		return getMinecraft().playerController;
	}

	public static RenderManager getRenderManager() {
		return getMinecraft().getRenderManager();
	}

	public static RenderGlobal getGlobalRenderer() {
		return getMinecraft().renderGlobal;
	}

	public static NetHandlerPlayClient getSendQueue() {
		return getMinecraft().thePlayer != null ? getMinecraft().thePlayer.sendQueue : null;
	}

	public static void addPacket(Packet packet) {
		getSendQueue().addToSendQueue(packet);
	}

	public static void sendPacket(Packet packet) {
		getSendQueue().getNetworkManager().sendPacket(packet);
	}



	public static void sendChat(String MSG) {
		sendPacket(new C01PacketChatMessage(MSG));
	}


	public static void addChat(ChatEnum mode, String message) {

		}


	public enum ChatEnum {
		ERROR, COMMAND, NOTIFY;
	}

}

package com.alan.clients.module.impl.player;

import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.component.impl.render.NotificationComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.BlockAABBEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockGlass;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "AutoClip", description = "Auto Fly In hyt skywars", category = Category.PLAYER)
public class AutoClip  extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Normal"))
            .add(new SubMode("Boost"))
            .setDefault("Normal");
    private Vector3d startPlayer;
    private boolean phasing;
    private BlockPos startPos;
    private int boostTick;

    @Override
    public void onEnable() {
        if (mode.getValue().getName().equalsIgnoreCase("Normal")) {
            startPlayer = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            startPos = new BlockPos(mc.thePlayer).down();
            phasing = true;
            BlinkComponent.setExempt(C08PacketPlayerBlockPlacement.class);
            BlinkComponent.blinking = true;
        }

        boostTick = 0;
    }

    @EventLink
    private final Listener<WorldChangeEvent> onWorld = event -> {
        phasing = false;
        boostTick = 0;
    };

    @Override
    public void onDisable() {
        if (startPos != null && !(mc.theWorld.getBlockState(startPos).getBlock() instanceof BlockAir)) {
            BlinkComponent.packets.forEach(packet -> {
                if (packet instanceof C03PacketPlayer) {
                    final C03PacketPlayer wrapped = (C03PacketPlayer) packet;

                    if (wrapped.moving) {
                        wrapped.x = startPlayer.getX();
                        wrapped.y = startPlayer.getY();
                        wrapped.z = startPlayer.getZ();
                    }
                }

                mc.getNetHandler().addToSendQueueUnregistered(packet);
            });
            BlinkComponent.packets.clear();

            mc.thePlayer.setPosition(startPlayer.getX(), startPlayer.getY(), startPlayer.getZ());
        }
        BlinkComponent.blinking = false;
        startPos = null;
    }

    @EventLink
    private final Listener<BlockAABBEvent> onBlockAABB = event -> {
        if (mode.getValue().getName().equalsIgnoreCase("Normal") && phasing)
            event.setBoundingBox(null);
    };

    @EventLink
    private final Listener<PreMotionEvent> onPreMotion = event -> {
        if (mode.getValue().getName().equalsIgnoreCase("Normal")) {
            if (mc.thePlayer.posY + 3.1 < startPos.getY()) {
                phasing = false;

                if (mc.theWorld.getBlockState(startPos).getBlock() instanceof BlockAir) {
                    BlinkComponent.blinking = false;
                    toggle();
                    NotificationComponent.post("Phase", "Operation successful!");
                }
            }
        } else {
            if (!phasing) startPos = new BlockPos(mc.thePlayer).up(2);

            if (mc.theWorld.getBlockState(startPos).getBlock() instanceof BlockGlass) {
                if (!phasing) {
                    phasing = true;
                    BlinkComponent.setExempt(C08PacketPlayerBlockPlacement.class);
                    BlinkComponent.blinking = true;
                    boostTick = 0;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ);
                }
                boostTick++;

                if (boostTick == 5) {
                    MoveUtil.strafe(5);
                }

                if (boostTick == 1000) {
                    BlinkComponent.packets.clear();
                    BlinkComponent.blinking = false;
                    mc.thePlayer.sendChatMessage("/hub");
                }
            } else {
                BlinkComponent.blinking = false;
            }
        }
    };

}

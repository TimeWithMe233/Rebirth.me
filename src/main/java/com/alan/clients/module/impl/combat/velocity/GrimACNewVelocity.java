package com.alan.clients.module.impl.combat.velocity;

import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.module.impl.combat.KillAura;
import com.alan.clients.module.impl.combat.Velocity;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class GrimACNewVelocity extends Mode<Velocity> {
    public GrimACNewVelocity(String name, Velocity parent) {
        super(name, parent);
    }
    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Block Spoof"))
            .add(new SubMode("Attack Reduce"))
            .add(new SubMode("1.17+"))
            .setDefault("Block Spoof");
    private final BooleanValue legitSprint = new BooleanValue("Legit Sprint", this, false, () -> !mode.getValue().getName().equalsIgnoreCase("attack reduce"));
    private final BooleanValue s08debug = new BooleanValue("S08 Check Debug", this, true);
    private int lastSprint = -1;


    @Override
    public void onDisable() {
        lastSprint = -1;
    }

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (mode.getValue().getName().equalsIgnoreCase("attack reduce") && legitSprint.getValue()) {
            if (lastSprint == 0) {
                lastSprint--;
                if (!MoveUtil.canSprint(true))
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            } else if (lastSprint > 0) {
                lastSprint--;
                if (mc.thePlayer.onGround && !MoveUtil.canSprint(true)) {
                    lastSprint = -1;
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                }
            }
        }
    };



    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        Packet<?> packet = event.getPacket();

        if (packet instanceof S08PacketPlayerPosLook && s08debug.getValue()) {
            ChatUtil.display("S08 Flags");
        }
        if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapped = (S12PacketEntityVelocity) packet;


            if (wrapped.getEntityID() == mc.thePlayer.getEntityId()) {

                switch (mode.getValue().getName().toLowerCase()) {
                    case "block spoof": {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.thePlayer), EnumFacing.UP));
                        mc.timer.lastSyncSysClock += 1;
                        event.setCancelled();

                        break;
                    }
                    case "1.17+": {
                        mc.getNetHandler().addToSendQueueUnregistered(new C03PacketPlayer.C06PacketPlayerPosLook(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ,
                                RotationComponent.rotations.x,
                                RotationComponent.rotations.y,
                                mc.thePlayer.onGround
                        ));
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(mc.thePlayer), EnumFacing.UP));
                        event.setCancelled();

                        break;
                    }
                    case "attack reduce": {
                        if (getModule(KillAura.class).target != null) {

                            if(mc.thePlayer.getDistanceToEntity(getModule(KillAura.class).target) >getModule(KillAura.class).range.getValue().doubleValue()){
                                return;
                            }

                            event.setCancelled();

                            if (!EntityPlayerSP.serverSprintState) {
                                if (legitSprint.getValue()) {
                                    if (lastSprint < 0) mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                                    lastSprint = 2;
                                } else {
                                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                                }
                            }

                            for (int i = 0;i < 8;i++) {
                                mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(getModule(KillAura.class).target, C02PacketUseEntity.Action.ATTACK));
                                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                            }


                            double velocityX = wrapped.motionX / 8000.0;
                            double velocityZ = wrapped.motionZ / 8000.0;

                            if (MathHelper.sqrt_double(velocityX * velocityX * velocityZ * velocityZ) <= 3F) {
                                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                            } else {
                                mc.thePlayer.motionX = velocityX * 0.078;
                                mc.thePlayer.motionZ = velocityZ * 0.078;
                            }

                            mc.thePlayer.motionY = wrapped.motionY / 8000.0;

                            if (!EntityPlayerSP.serverSprintState && !legitSprint.getValue())
                                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                        }

                        break;
                    }
                }
            }
        }
    };
}

package com.alan.clients.module.impl.player;


import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.util.RandomUtil;
import com.alan.clients.util.RayCastUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.rotation.RotationUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.BoundsNumberValue;
import com.alan.clients.value.impl.ListValue;
import com.alan.clients.value.impl.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "ChestAura", description = "Chest Aura", category = Category.PLAYER)
public class ChestAura extends Module {

    private final NumberValue range = new NumberValue("Range", this, 4, 1, 6, 0.1);
    private final BooleanValue rotation = new BooleanValue("Rotation", this, false);
    private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation Speed", this, 5, 10, 1, 10, 1, () -> !rotation.getValue());
    private final ListValue<MovementFix> movementCorrection = new ListValue<>("Movement Correction", this, () -> !rotation.getValue());
    private final BoundsNumberValue delay = new BoundsNumberValue("Delay", this, 50, 100, 0, 5000, 50);

    private StopWatch stopWatch = new StopWatch();
    private long nextWait = 0;
    private List<BlockPos> found = new ArrayList<>();

    public ChestAura() {
        for (MovementFix movementFix : MovementFix.values()) {
            movementCorrection.add(movementFix);
        }

        movementCorrection.setDefault(MovementFix.OFF);
    }

    @Override
    protected void onEnable() {
        stopWatch.reset();
        found.clear();
    }

    @EventLink
    private final Listener<WorldChangeEvent> onWorld = event -> {
        found.clear();
        stopWatch.reset();
    };

    @EventLink
    private final Listener<PreMotionEvent> onPreMotion = event -> {
        if (!stopWatch.finished(nextWait) || mc.currentScreen != null) return;
        int reach = range.getValue().intValue();

        for (int x = -reach;x <= reach; x++) {
            for (int y = -reach;y <= reach; y++) {
                for (int z = -reach;z <= reach; z++) {
                    final BlockPos blockPos = new BlockPos(mc.thePlayer).add(x, y, z);
                    if (found.contains(blockPos)) continue;

                    final Block block = PlayerUtil.blockRelativeToPlayer(x, y, z);
                    final Vector3d position = new Vector3d(InstanceAccess.mc.thePlayer.posX + x, InstanceAccess.mc.thePlayer.posY + y, InstanceAccess.mc.thePlayer.posZ + z);

                    if (block instanceof BlockChest) {
                        final Vector2f vector2f = RotationUtil.calculate(position);

                        if (rotation.getValue()) {
                            RotationComponent.setRotations(vector2f, RandomUtil.nextInt(rotationSpeed.getValue().intValue(), rotationSpeed.getSecondValue().intValue()), movementCorrection.getValue());

                            final MovingObjectPosition result = RayCastUtil.rayCast(RotationComponent.rotations, reach);
                            if (result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.theWorld.getBlockState(result.getBlockPos()).getBlock() instanceof BlockChest && result.getBlockPos().equals(blockPos)) {
                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, SlotComponent.getItemStack(), result.getBlockPos(), result.sideHit, result.hitVec);
                                found.add(blockPos);
                                nextWait = RandomUtil.nextInt(delay.getValue().intValue(), delay.getSecondValue().intValue());
                                stopWatch.reset();
                                return;
                            }
                        } else {
                            final MovingObjectPosition result = RayCastUtil.rayCast(vector2f, reach);

                            if (result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.theWorld.getBlockState(result.getBlockPos()).getBlock() instanceof BlockChest) {
                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, SlotComponent.getItemStack(), result.getBlockPos(), result.sideHit, result.hitVec);
                                found.add(blockPos);
                                nextWait = RandomUtil.nextInt(delay.getValue().intValue(), delay.getSecondValue().intValue());
                                stopWatch.reset();
                                return;
                            }
                        }
                    }
                }
            }
        }
    };
}

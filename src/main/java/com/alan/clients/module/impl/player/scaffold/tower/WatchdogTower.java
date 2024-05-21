package com.alan.clients.module.impl.player.scaffold.tower;

import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.Mode;

public class WatchdogTower extends Mode<Scaffold> {
    public WatchdogTower(String name, Scaffold parent) {
        super(name, parent);
    }

    private int towerTick = 0;

    @Override
    public void onEnable() {
        towerTick = 0;
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        if (mc.gameSettings.keyBindJump.isKeyDown() && PlayerUtil.blockNear(2)) {
            if (MoveUtil.isMoving()) {
                towerTick++;

                if (mc.thePlayer.onGround)
                    towerTick = 0;

                mc.thePlayer.motionY = 0.41965;
                mc.thePlayer.motionX = Math.min(mc.thePlayer.motionX, 0.265);
                mc.thePlayer.motionZ = Math.min(mc.thePlayer.motionZ, 0.265);

                if (towerTick == 1)
                    mc.thePlayer.motionY = 0.33;
                else if (towerTick == 2)
                    mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                else if (towerTick >= 3)
                    towerTick = 0;
            } else {
                towerTick = 0;
                if (mc.thePlayer.onGround) mc.thePlayer.jump();
            }
        } else {
            towerTick = 0;
        }
    };
}

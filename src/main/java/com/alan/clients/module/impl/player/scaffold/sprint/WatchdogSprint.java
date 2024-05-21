package com.alan.clients.module.impl.player.scaffold.sprint;

import com.alan.clients.component.impl.player.BlinkComponent;
import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;

public class WatchdogSprint extends Mode<Scaffold> {
    public WatchdogSprint(String name, Scaffold parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        BlinkComponent.blinking = true;
        mc.gameSettings.keyBindSprint.setPressed(true);
    }

    @EventLink
    public final Listener<PreMotionEvent> onPreMotion = event -> {
        mc.gameSettings.keyBindSprint.setPressed(true);
        if (mc.thePlayer.ticksExisted % 3 == 0)
            BlinkComponent.dispatch();
    };

    @Override
    public void onDisable() {
        BlinkComponent.dispatch();
        BlinkComponent.blinking = false;
    }
}

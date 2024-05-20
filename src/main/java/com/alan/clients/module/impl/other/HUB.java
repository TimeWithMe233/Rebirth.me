package com.alan.clients.module.impl.other;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.interfaces.InstanceAccess;

@ModuleInfo(name = "HUB", category = Category.OTHER,description = "Enable module U can Hub")
public class HUB extends Module {
    @Override
    public void onEnable() {
            InstanceAccess.mc.thePlayer.sendChatMessage("大事不好 陈安见给我打电话了 我先撤了");
            InstanceAccess.mc.thePlayer.sendChatMessage("/hub");
            setEnabled(false);

    }
    @Override
    public void onDisable(){
        ChatUtil.display("Runaway");
    }
}

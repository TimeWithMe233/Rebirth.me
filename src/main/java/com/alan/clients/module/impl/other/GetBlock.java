package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.render.NotificationComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;

@Rise
@ModuleInfo(name = "Get Block", description = "Give you some block",category = Category.OTHER)
public class GetBlock extends Module {
    private final ModeValue blockmode = new ModeValue("Block Mode", this) {{
        add(new SubMode("Stone"));
        add(new SubMode("Diamond_Block"));
        add(new SubMode("Glass"));
        setDefault("Glass");
    }};
    @Override
    public void onEnable() {
        InstanceAccess.mc.thePlayer.sendChatMessage("/give " + blockmode.getValue().getName().toLowerCase() + " 64");
        setEnabled(false);

    }
    @Override
    public void onDisable(){
        NotificationComponent.post("GetBlock","Get 64 blocks of "+ blockmode.getValue().getName() +" for You",500);
    }
}

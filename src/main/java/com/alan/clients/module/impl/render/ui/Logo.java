package com.alan.clients.module.impl.render.ui;

import com.alan.clients.Client;
import com.alan.clients.module.impl.render.AllGui;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.ServerUtils;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.DragValue;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class Logo extends Mode<AllGui> {
    private final DragValue position = new DragValue("", getParent(), new Vector2d(200, 200), true);
    private Color logoColor;

    public Logo(String name, AllGui parent) {
        super(name, parent);
    }
    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {
        String text = " | " + Client.VERSION + " | " + mc.thePlayer.getCommandSenderName() + " | " + ServerUtils.getRemoteIp() + " | " + "Fps:"+ Minecraft.getDebugFPS();
        int sb = 6 * 25 - 5;
        int left = FontManager.getProductSansRegular(20).width(text);
        position.scale = new Vector2d(200, 100);
        double x = this.position.position.x;
        double y = this.position.position.y;
        // Don't draw if the F3 menu is open
        if (mc.gameSettings.showDebugInfo) return;
        logoColor = this.getTheme().getFirstColor();
        Color color1 = ColorUtil.mixColors(getTheme().getFirstColor(), getTheme().getFirstColor(), getTheme().getBlendFactor(new Vector2d(0, y)));
        Color color2 = ColorUtil.mixColors(getTheme().getFirstColor(), getTheme().getFirstColor(), getTheme().getBlendFactor(new Vector2d(0, y + position.scale.y * 8.75)));
        // blur
        NORMAL_BLUR_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(x + 10, y + 10, left + position.scale.x - sb + 5,
                    position.scale.y - 80, 0.2, Color.BLACK);
        });
        //shadow
        NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(x + 10, y + 10, left + position.scale.x - sb + 5, position.scale.y - 80, 0.2,  Color.BLACK);
            RenderUtil.drawRoundedGradientRect(x + 10, y + 8, left + position.scale.x - sb + 5, position.scale.y - 96, 0.2, color1,color2,true);
            RenderUtil.drawRoundedGradientRect(x + 10, y + 8, left + position.scale.x - sb + 5, position.scale.y - 96, 0.2, color2,color1,true);
        });
        RenderUtil.roundedRectangle(x + 10, y + 10, left + position.scale.x - sb + 5,
                position.scale.y - 80, 0.2, new Color(0,0,0,50));
        RenderUtil.drawRoundedGradientRect(x + 10, y + 8, left + position.scale.x - sb + 5, position.scale.y - 96, 0.2, color1,color2,true);
        RenderUtil.drawRoundedGradientRect(x + 10, y + 8, left + position.scale.x - sb + 5, position.scale.y - 96, 0.2, color2,color1,true);
        //font
        NORMAL_RENDER_RUNNABLES.add(() -> {
            FontManager.getProductSansRegular(20).drawStringWithShadow(Client.NAME, x + 13, y + 17, logoColor.getRGB());
            FontManager.getProductSansRegular(20).drawStringWithShadow(text, x + 67, y + 17, Color.WHITE.getRGB());
        });
    };
}

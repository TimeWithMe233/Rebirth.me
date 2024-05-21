package com.alan.clients.module.impl.render;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.exploit.disabler.*;
import com.alan.clients.module.impl.render.ui.*;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.ServerUtils;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.DragValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Rise
@ModuleInfo(name = "module.combat.gui.name", description = "module.combat.gui.description", category = Category.RENDER)
public class AllGui extends Module {
    public static AllGui INSTANCE;
    private final Font productSans = FontManager.getProductSansRegular(20);
    private final DragValue position = new DragValue("", this, new Vector2d(200, 200), true);
    private final BooleanValue health = new BooleanValue("Health", this, false);
    private final BooleanValue logo = new BooleanValue("Logo", this, false);
    private Color logoColor;
    public AllGui() {
        INSTANCE = this;
    }
    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {
        String text = " | " + Client.VERSION + " | " + mc.thePlayer.getCommandSenderName() + " | " + ServerUtils.getRemoteIp() + " | " + "Fps:"+ Minecraft.getDebugFPS();
        int sb = 6 * 25 - 5;
        int left = productSans.width(text);
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        double x = this.position.position.x;
        double y = this.position.position.y;
        position.scale = new Vector2d(200, 100);
        // Don't draw if the F3 menu is open
        if (mc.gameSettings.showDebugInfo) return;
        Color logoColor = this.getTheme().getFirstColor();
        Color rightColor = this.getTheme().getSecondColor();
        Color color1 = ColorUtil.mixColors(getTheme().getFirstColor(), getTheme().getFirstColor(), getTheme().getBlendFactor(new Vector2d(0, y)));
        Color color2 = ColorUtil.mixColors(getTheme().getFirstColor(), getTheme().getFirstColor(), getTheme().getBlendFactor(new Vector2d(0, y + position.scale.y * 8.75)));
        if (logo.getValue()) {
        // blur
        NORMAL_BLUR_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(x + 10, y + 10, left + position.scale.x - sb + 5,
                    position.scale.y - 80, 0.2, Color.BLACK);
        });
        //shadow
        NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
            RenderUtil.roundedRectangle(x + 10, y + 10, left + position.scale.x - sb + 5, position.scale.y - 80, 0.2,  Color.BLACK);
            RenderUtil.drawRoundedGradientRect(x + 10, y + 8, left + position.scale.x - sb + 5, position.scale.y - 96, 0.2, rightColor,logoColor,false);
            RenderUtil.drawRoundedGradientRect(x + 10, y + 8, left + position.scale.x - sb + 5, position.scale.y - 96, 0.2, logoColor,rightColor,false);

        });
        RenderUtil.roundedRectangle(x + 10, y + 10, left + position.scale.x - sb + 5,
                position.scale.y - 80, 0.2, new Color(0,0,0,50));
 //font
        NORMAL_RENDER_RUNNABLES.add(() -> {
            productSans.drawStringWithShadow(Client.NAME, x + 13, y + 17, logoColor.getRGB());
            productSans.drawStringWithShadow(text, x + 67, y + 17, Color.WHITE.getRGB());
            RenderUtil.drawRoundedGradientRect(x + 10, y + 8, left + position.scale.x - sb + 5, position.scale.y - 96, 0.2, rightColor,logoColor,false);
            RenderUtil.drawRoundedGradientRect(x + 10, y + 8, left + position.scale.x - sb + 5, position.scale.y - 96, 0.2, logoColor,rightColor,false);
        });
      }
        if (health.getValue()) {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderHelper.enableGUIStandardItemLighting();
            mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/icons.png"));
            GL11.glDisable(2929);
            GL11.glEnable(3042);
            GL11.glDepthMask(false);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            final float maxHealth = mc.thePlayer.getMaxHealth();
            for (int n = 0; n < maxHealth / 2.0f; ++n) {
                mc.ingameGUI.drawTexturedModalRect(scaledResolution.getScaledWidth() / 2 + 1f - maxHealth / 2.0f * 10.0f / 2.0f + n * 10, (scaledResolution.getScaledHeight() / 2 - 20 + 30), 16, 0, 9, 9);
            }
            final float health = mc.thePlayer.getHealth();
            for (int n2 = 0; n2 < health / 2.0f; ++n2) {
                mc.ingameGUI.drawTexturedModalRect(scaledResolution.getScaledWidth() / 2 + 1f - maxHealth / 2.0f * 10.0f / 2.0f + n2 * 10, (scaledResolution.getScaledHeight() / 2 - 20 + 30), 52, 0, 9, 9);
            }
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glEnable(2929);
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            RenderHelper.disableStandardItemLighting();
        }
    };
}
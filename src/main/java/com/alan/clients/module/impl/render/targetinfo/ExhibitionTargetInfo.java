package com.alan.clients.module.impl.render.targetinfo;

import com.alan.clients.module.impl.render.TargetInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.GLUtil;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.value.Mode;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

import static com.alan.clients.util.animation.Easing.EASE_IN_BACK;
import static com.alan.clients.util.animation.Easing.EASE_OUT_ELASTIC;

public class ExhibitionTargetInfo extends Mode<TargetInfo> {

    public ExhibitionTargetInfo(String name, TargetInfo parent) {
        super(name, parent);
    }
    private TargetInfo targetInfoModule;

    private final Font sfuiBold18 = FontManager.getNunitoLightCN(18);
    private final Font sfuiBold13 = FontManager.getNunitoLightCN(12);
    private final Font productSansS = FontManager.getProductSansRegular(18);
    private Animation openingAnimation = new Animation(EASE_OUT_ELASTIC, 500);

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {

        if (this.targetInfoModule == null) {
            this.targetInfoModule = this.getModule(TargetInfo.class);
        }

        Entity target = this.targetInfoModule.target;
        if (target == null) return;

        boolean out = (!this.targetInfoModule.inWorld || this.targetInfoModule.stopwatch.finished(1000));
        openingAnimation.setDuration(out ? 400 : 850);
        openingAnimation.setEasing(out ? EASE_IN_BACK : EASE_OUT_ELASTIC);
        openingAnimation.run(out ? 0 : 1);

        if (openingAnimation.getValue() <= 0) return;

        double x = this.targetInfoModule.position.x;
        double y = this.targetInfoModule.position.y;
        double width = Math.max(135, FontManager.getNunitoLightCN(20).width("Name: " + "bruh") + 60);
        double height = 46;
        NORMAL_POST_RENDER_RUNNABLES.add(() -> {
            GlStateManager.pushMatrix();
            GlStateManager.translate((x + width / 2) * (1 - openingAnimation.getValue()), (y + height / 2) * (1 - openingAnimation.getValue()), 0);
            GlStateManager.scale(openingAnimation.getValue(), openingAnimation.getValue(), 0);

            Color darkest = com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render.ColorUtil.applyOpacity(new Color(10, 10, 10), 1F);
            Color secondDarkest = com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render.ColorUtil.applyOpacity(new Color(22, 22, 22), 1F);
            Color lightest = com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render.ColorUtil.applyOpacity(new Color(44, 44, 44), 1F);
            Color middleColor = com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render.ColorUtil.applyOpacity(new Color(34, 34, 34), 1F);
            Color textColor = com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render.ColorUtil.applyOpacity(Color.WHITE, 1F);
            Gui.drawRect2(x - 3.5, y - 3.5, width + 7, height + 7, darkest.getRGB());
            Gui.drawRect2(x - 3, y - 3, width + 6, height + 6, middleColor.getRGB());
            Gui.drawRect2(x - 1, y - 1, width+ 2, height + 2, lightest.getRGB());
            Gui.drawRect2(x, y, width, height, secondDarkest.getRGB());

            float size = (float) (height - 6);
            Gui.drawRect2(x + 3, y + 3, .5, size, lightest.getRGB());
            Gui.drawRect2(x + 3, y + 3 + size, size, .5, lightest.getRGB());
            Gui.drawRect2(x + 3 + size, y + 3, .5f, size + .5f, lightest.getRGB());
            Gui.drawRect2(x + 3, y + 3, size, .5, lightest.getRGB());


            productSansS.drawString(target.getCommandSenderName(), x + 8 + size, y + 6, textColor.getRGB());
            float healthValue = (((AbstractClientPlayer) target).getHealth() + ((AbstractClientPlayer) target).getAbsorptionAmount()) / (((AbstractClientPlayer) target).getMaxHealth() + ((AbstractClientPlayer) target).getAbsorptionAmount());

            Color healthColor = healthValue > .5f ? com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render.ColorUtil.interpolateColorC(new Color(255, 255, 10), new Color(10, 255, 10), (healthValue - .5f) / .5f) :
                    com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render.ColorUtil.interpolateColorC(new Color(255, 10, 10), new Color(255, 255, 10), healthValue * 2);

            healthColor = com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render.ColorUtil.applyOpacity(healthColor, 1F);

            float healthBarWidth = (float) (width - (size + 12));
            Gui.drawRect2(x + 8 + size, y + 15, healthBarWidth, 5, darkest.getRGB());
            Gui.drawRect2(x + 8 + size + .5, y + 15.5F, healthBarWidth - 1, 4, new Color(com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render.ColorUtil.interpolateColor(darkest, healthColor, .2f)).getRGB());

            float heathBarActualWidth = healthBarWidth - 1;
            Gui.drawRect2(x + 8 + size + .5, y + 15.5F, heathBarActualWidth * healthValue, 4, healthColor.getRGB());

            float increment = heathBarActualWidth / 11;
            for (int i = 1; i < 11; i++) {
                Gui.drawRect2(x + 8 + size + (increment * i), y + 15.5F, .5f, 4, darkest.getRGB());
            }

         sfuiBold13.drawString("HP: " + MathUtil.round(((AbstractClientPlayer) target).getHealth() + ((AbstractClientPlayer) target).getAbsorptionAmount(), 1) + " | Dist: " + MathUtil.round(mc.thePlayer.getDistanceToEntity(target), 1),
                    x + 8 + size, y + 25, textColor.getRGB());


            float seperation = healthBarWidth / 5;
            RenderUtil.resetColor();
            GuiInventory.drawEntityOnScreen((int) (x + 3 + size / 2f), (int) (y + size + 1), 18, target.rotationYaw, -target.rotationPitch, (EntityLivingBase) target);

            GLUtil.startBlend();

            RenderHelper.enableGUIStandardItemLighting();
            for (int i = 0; i <= 3; i++) {
                if (((AbstractClientPlayer) target).getCurrentArmor(i) == null) continue;
                RenderUtil.resetColor();
                GLUtil.startBlend();
                RenderUtil.color(textColor.getRGB());
                mc.getRenderItem().renderItemAndEffectIntoGUI(((AbstractClientPlayer) target).getCurrentArmor(i), (int) (x + size + 7 + (seperation * (3 - i))), (int) (y + 28));
                GLUtil.endBlend();
            }

            if (((AbstractClientPlayer) target).getHeldItem() != null) {
                GLUtil.startBlend();
                RenderUtil.resetColor();
                RenderUtil.color(textColor.getRGB());
                mc.getRenderItem().renderItemAndEffectIntoGUI(((AbstractClientPlayer) target).getHeldItem(), (int) (x + size + 7 + (seperation * 4)), (int) (y + 28));
                GLUtil.endBlend();
            }
            RenderHelper.disableStandardItemLighting();

            GlStateManager.popMatrix();
        });
    };
}

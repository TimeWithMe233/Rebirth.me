package com.alan.clients.module.impl.render.targetinfo;

import com.alan.clients.module.impl.render.TargetInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.render.StencilUtil;
import com.alan.clients.value.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.alan.clients.util.animation.Easing.EASE_IN_BACK;
import static com.alan.clients.util.animation.Easing.EASE_OUT_ELASTIC;

/**
 * This file is part of Amireux project.
 * Copyright 2023 Amireux
 * All Rights Reserved.
 * <p>
 * 2023/7/23 16:45
 */
public class MoonTargetInfo extends Mode<TargetInfo> {
    private TargetInfo targetInfoModule;


    private final Font sfuiBold18 = FontManager.getNunitoLightCN(18);
    private final Font sfuiBold13 = FontManager.getNunitoLightCN(13);

    private static double lastP = 0, diffP = 0;

    private static final Animation animation = new Animation(Easing.EASE_IN_BACK, 2 * 150);

    public MoonTargetInfo(String name, TargetInfo parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {
        if (this.targetInfoModule == null) {
            this.targetInfoModule = this.getModule(TargetInfo.class);
        }

        boolean out = (!this.targetInfoModule.inWorld || this.targetInfoModule.stopwatch.finished(1000));
        animation.run(out ? 0 : 1);
        animation.setEasing(out ? EASE_IN_BACK : EASE_OUT_ELASTIC);
        animation.setDuration(2 * 150);

        EntityPlayer e = (EntityPlayer) this.targetInfoModule.target;

        if (e == null) {
            return;
        }

        GL11.glPushMatrix();
        Minecraft mc = Minecraft.getMinecraft();
        RenderUtil.scale(mc);
        float hp = e.getHealth() + e.getAbsorptionAmount();
        final float maxHP = e.getMaxHealth() + e.getAbsorptionAmount() - 0.05f;
        int i = 0;

        for (int b = 0; b < e.inventory.armorInventory.length; b++) {
            final ItemStack armor = e.inventory.armorInventory[b];

            if (armor != null) {
                i++;
            }
        }

        if (e.getCurrentEquippedItem() != null) {
        }

        float rectLength = 35 + sfuiBold18.width(e.getCommandSenderName()) + 40, health = (float) (Math.round(hp * 100.0) / 100.0);

        if (health > maxHP) {
            health *= maxHP / health;
        }

        float amplifier = 100 / maxHP, percent = health * amplifier, space = (rectLength - 50) / 100; //
        ScaledResolution sr = new ScaledResolution(mc);

        if (this.targetInfoModule.position.x > sr.getScaledWidth() - 50) {
            this.targetInfoModule.position.x = sr.getScaledWidth() - 50;
        }

        if (this.targetInfoModule.position.y > sr.getScaledHeight() - 50) {
            this.targetInfoModule.position.y = sr.getScaledHeight() - 50;
        }


        final double i2 = this.targetInfoModule.position.x;
        final double i1 = this.targetInfoModule.position.y;

        if (percent < lastP) {
            diffP = lastP - percent;
        }
        lastP = percent;
        if (diffP > 0) {
            diffP = diffP + (0 - diffP) * 0.05f;
        }

        diffP = MathHelper.clamp_double(diffP, 0, 100 - percent);


//        mc.getTextureManager().bindTexture(e.getLocationSkin());
        double x = this.targetInfoModule.position.x;
        double y = this.targetInfoModule.position.y;


        RenderUtil.scaleStart((float) (i2 + (rectLength - 7) / 2), (float) i1 + 40.5f / 2, (float) animation.getValue());
        RenderUtil.drawRoundedGradientRect(i2, i1 - 2, rectLength - 7, 40.5f, 8, new Color(0, 0, 0, 100), new Color(0, 0, 0, 100), false);
        GL11.glColor4f(1, 1, 1, 1);

        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderUtil.roundedRectangle(i2 + 2.5f, i1 + 0.5f, 33.5F, 35F, 5F, Color.WHITE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        StencilUtil.bindReadStencilBuffer(1);
        drawHead(((AbstractClientPlayer) e).getLocationSkin(), (float) (i2 + 2.5f), (float) (i1 + 0.5f), 1f, 35, 35, 1f, 1f, 1F);
        StencilUtil.uninitStencilBuffer();

//        int hudColor = getColor();
//        int hudColor2 = getthudColor(255, 255);
//        RenderUtils.drawRoundedRect(i2 + 40, i1 + 24.5f, (100 * space), 8.5f, 5, new Color(0, 0, 0, 150).getRGB());
        RenderUtil.roundedRectangle(i2 + 40, i1 + 24.5f, (100 * space), 8.5f, 5, new Color(0, 0, 0, 150));
        String text = String.format("%.1f", e.getHealth());

        RenderUtil.drawRoundedGradientRect(i2 + 40, i1 + 24.5f, (float) (percent * space + diffP * space), 8.5f, 3, this.getTheme().getFirstColor(), this.getTheme().getSecondColor(), false);
//        RenderUtils.drawRoundedRect(i2 + 40, i1 + 24.5f, (float) (percent * space + diffP * space), 8.5f, 5, new Color(hudColor).getRGB());
        sfuiBold13.drawStringWithShadow(text + "HP", i2 + 40,
                i1 + 15, 0xffffffff);
        sfuiBold18.drawStringWithShadow(e.getCommandSenderName(), i2 + 40, i1 + 4, 0xffffffff);
        //   mc.fontRendererCrack.drawString(String.format("%.1f", (e.getHealth() + e.getAbsorptionAmount()) / 2), i2 + 41, i1 + 27, 0xffffffff, true);
        //   mc.fontRendererCrack.drawString(" \u2764", i2 + 40 + mc.fontRendererCrack.getStringWidth(String.format("%.1f", (e.getHealth() + e.getAbsorptionAmount()) / 2.0F)), i1 + 27, hudColor, true);
        RenderUtil.scaleEnd();

        GL11.glPopMatrix();
    };

    public static void drawHead(ResourceLocation skin, float x, float y, float scale, int width, int height, float red, float green, float blue) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0F);
        GL11.glScalef(scale, scale, scale);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glColor4f(red, green, blue, 1F);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(0, 0, 8F, 8F, 8, 8, width, height,
                64F, 64F);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}

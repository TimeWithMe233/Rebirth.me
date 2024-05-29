package com.alan.clients.util.render;

import com.alan.clients.util.BlockUtil;
import com.alan.clients.util.GLUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.shader.RiseShaders;
import com.alan.clients.util.vector.Vector3d;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static com.alan.clients.util.math.MathUtil.interpolate;
import static com.alan.clients.util.render.ColorUtil.glColor;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

@UtilityClass
public final class RenderUtil implements InstanceAccess {

    private static final Frustum FRUSTUM = new Frustum();
    private static final RenderManager RENDER_MANAGER = mc.getRenderManager();
    private static final Map<Integer, Boolean> glCapMap = new HashMap<>();
    public static long lastFrame = 0;

    public static double ticks = 0;
    public static final int GENERIC_SCALE = 22;
    public static int deltaTime;

    public Vector3d getCameraVector() {
        return new Vector3d(
                -RENDER_MANAGER.renderPosX,
                -RENDER_MANAGER.renderPosY,
                -RENDER_MANAGER.renderPosZ
        );
    }

    /**
     * Better to use gl state manager to avoid bugs
     */
    public void start() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    /**
     * Better to use gl state manager to avoid bugs
     */
    public void stop() {
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public void rectangle(final double x, final double y, final double width, final double height, final Color color) {
        start();

        if (color != null) {
            ColorUtil.glColor(color);
        }

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);
        GL11.glEnd();

        stop();
    }

    public void rainbowRectangle(final double x, final double y, final double width, final double height) {
        start();

        GL11.glBegin(GL11.GL_QUADS);

        for (double position = x; position <= x + width; position += 0.5) {
            color(Color.getHSBColor((float) ((position - x) / width), 1, 1));

            GL11.glVertex2d(position, y);
            GL11.glVertex2d(position + 0.5f, y);
            GL11.glVertex2d(position + 0.5f, y + height);
            GL11.glVertex2d(position, y + height);
        }

        GL11.glEnd();

        stop();
    }

    public void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }
    public void rectangle(final double x, final double y, final double width, final double height) {
        rectangle(x, y, width, height, null);
    }

    public void centeredRectangle(final double x, final double y, final double width, final double height, final Color color) {
        rectangle(x - width / 2, y - height / 2, width, height, color);
    }

    public void centeredRectangle(final double x, final double y, final double width, final double height) {
        rectangle(x - width / 2, y - height / 2, width, height, null);
    }

    public void verticalGradient(final double x, final double y, final double width, final double height, final Color topColor, final Color bottomColor) {
        start();
        GlStateManager.alphaFunc(516, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        ColorUtil.glColor(topColor);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);

        ColorUtil.glColor(bottomColor);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        stop();
    }
    public void begin(final int glMode) {
        GL11.glBegin(glMode);
    }

    public void gradientCentered(double x, double y, final double width, final double height, final Color topColor, final Color bottomColor) {
        x -= width / 2;
        y -= height / 2;
        verticalGradient(x, y, width, height, topColor, bottomColor);
    }

    public void horizontalGradient(final double x, final double y, final double width, final double height, final Color leftColor, final Color rightColor) {
        start();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);

        ColorUtil.glColor(leftColor);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y + height);

        ColorUtil.glColor(rightColor);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x + width, y);

        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        stop();
    }

    public void horizontalCenteredGradient(final double x, final double y, final double width, final double height, final Color leftColor, final Color rightColor) {
        horizontalGradient(x - width / 2, y - height / 2, width, height, leftColor, rightColor);
    }

    public void image(final ResourceLocation imageLocation, final float x, final float y, final float width, final float height, final Color color) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        color(color);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        mc.getTextureManager().bindTexture(imageLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.resetColor();
        GlStateManager.disableBlend();
    }




    public void image(final ResourceLocation imageLocation, final double x, final double y, final double width, final double height, Color color) {
        image(imageLocation, (float) x, (float) y, (float) width, (float) height, color);
    }

    public void image(final ResourceLocation imageLocation, final float x, final float y, final float width, final float height) {
        image(imageLocation, x, y, width, height, Color.WHITE);
    }

    public void image(final ResourceLocation imageLocation, final double x, final double y, final double width, final double height) {
        image(imageLocation, (float) x, (float) y, (float) width, (float) height);
    }

    public static void dropShadow(final int loops, final double x, final double y, final double width, final double height, final double opacity, final double edgeRadius) {
        GlStateManager.alphaFunc(516, 0);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        for (float margin = 0; margin <= loops / 2f; margin += 0.5f) {
            roundedRectangle(x - margin / 2f, y - margin / 2f,
                    width + margin, height + margin, edgeRadius,
                    new Color(0, 0, 0, (int) Math.max(0.5f, (opacity - margin * 1.2) / 5.5f)));
        }
    }

    public void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public void color(Color color, final int alpha) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
    }

    public void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        if (!filled) GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
        {
            for (double i = 0; i <= amountOfSides / 4; i++) {
                final double angle = i * 4 * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final boolean filled) {
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final Color color) {
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides) {
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, color);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public void circle(final double x, final double y, final double radius, final boolean filled, final Color color) {
        polygon(x, y, radius, 360, filled, color);
    }
    public void circle(final double x, final double y, final double radius, final double sides, final boolean filled, final Color color) {
        polygon(x, y, radius, sides, filled, color);
    }

    public void circle(final double x, final double y, final double radius, final boolean filled) {
        polygon(x, y, radius, 360, filled);
    }

    public void circle(final double x, final double y, final double radius, final Color color) {
        polygon(x, y, radius, 360, color);
    }

    public void circle(final double x, final double y, final double radius) {
        polygon(x, y, radius, 360);
    }
    public void circleCentered(double x, double y, final double radius, final boolean filled, final Color color) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360, filled, color);
    }

    public void circleCentered(double x, double y, final double radius, final boolean filled) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360, filled);
    }

    public void circleCentered(double x, double y, final double radius, final boolean filled, final int sides) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, sides, filled);
    }

    public void circleCentered(double x, double y, final double radius, final Color color) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360, color);
    }

    public void circleCentered(double x, double y, final double radius) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360);
    }

    public void triangle(final double x, final double y, final double sideLength, final boolean filled,
                         final Color color) {
        polygon(x, y, sideLength, 3, filled, color);
    }

    public void triangle(final double x, final double y, final double sideLength, final boolean filled) {
        polygon(x, y, sideLength, 3, filled);
    }

    public void triangle(final double x, final double y, final double sideLength, final Color color) {
        polygon(x, y, sideLength, 3, color);
    }

    public void triangle(final double x, final double y, final double sideLength) {
        polygon(x, y, sideLength, 3);
    }

    public void triangleCentered(double x, double y, final double sideLength, final boolean filled,
                                 final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, filled, color);
    }

    public void triangleCentered(double x, double y, final double sideLength, final boolean filled) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, filled);
    }

    public void triangleCentered(double x, double y, final double sideLength, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, color);
    }

    public void triangleCentered(double x, double y, final double sideLength) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3);
    }

    public void renderItemIcon(final double x, final double y, final ItemStack itemStack) {
        if (itemStack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);

            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }

    public static void drawRoundedGradientRect(double x, double y, double width, double height, double radius, Color firstColor, Color secondColor, boolean vertical) {
        RiseShaders.RGQ_SHADER.draw(x, y, width, height, radius, firstColor, secondColor, vertical);
    }

    public void roundedRectangle(double x, double y, double width, double height, double radius, Color color) {
        RiseShaders.RQ_SHADER.draw(x, y, width, height, radius, color);
    }

    public void roundedOutlineRectangle(double x, double y, double width, double height, double radius, double borderSize, Color color) {
        RiseShaders.ROQ_SHADER.draw(x, y, width, height, radius, borderSize, color);
    }

    public void roundedOutlineGradientRectangle(double x, double y, double width, double height, double radius, double borderSize, Color color1, Color color2) {
        RiseShaders.ROGQ_SHADER.draw(x, y, width, height, radius, borderSize, color1, color2);
    }

    public void end() {
        GL11.glEnd();
    }

    public void circle(final double x, final double y, final float radius) {
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glPointSize(radius * 4);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_POINT_SMOOTH);
    }

    public void quad(final double x, final double y, final double width, final double height, final Color color) {
        ColorUtil.glColor(color);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);
        GL11.glEnd();
    }

    public void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = mc.scaledResolution;
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }


    public void drawLine(double x, double y, double z, double x1, double y1, double z1, final Color color, final float width) {
        x = x - mc.getRenderManager().renderPosX;
        x1 = x1 - mc.getRenderManager().renderPosX;
        y = y - mc.getRenderManager().renderPosY;
        y1 = y1 - mc.getRenderManager().renderPosY;
        z = z - mc.getRenderManager().renderPosZ;
        z1 = z1 - mc.getRenderManager().renderPosZ;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(width);

        color(color);
        GL11.glBegin(2);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public boolean isInViewFrustrum(final Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = mc.getRenderViewEntity();
        FRUSTUM.setPosition(current.posX, current.posY, current.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(bb);
    }

    public static Framebuffer createFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {

            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }

            return new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        }
        return framebuffer;
    }

    public static Vec3 getRenderPos(double x, double y, double z) {

        x -= mc.getRenderManager().renderPosX;
        y -= mc.getRenderManager().renderPosY;
        z -= mc.getRenderManager().renderPosZ;

        return new Vec3(x, y, z);
    }

    public static void glVertex3D(Vec3 vector3d) {
        GL11.glVertex3d(vector3d.xCoord, vector3d.yCoord, vector3d.zCoord);
    }

    public static void drawBoundingBox(final AxisAlignedBB aa) {

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        end();

        glBegin(GL_QUADS);
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.maxZ));
        glVertex3D(getRenderPos(aa.minX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.minX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.minZ));
        glVertex3D(getRenderPos(aa.maxX, aa.maxY, aa.maxZ));
        glVertex3D(getRenderPos(aa.maxX, aa.minY, aa.maxZ));
        end();
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 0xFF) / 255.0F;
        float f1 = (col1 >> 16 & 0xFF) / 255.0F;
        float f2 = (col1 >> 8 & 0xFF) / 255.0F;
        float f3 = (col1 & 0xFF) / 255.0F;

        float f4 = (col2 >> 24 & 0xFF) / 255.0F;
        float f5 = (col2 >> 16 & 0xFF) / 255.0F;
        float f6 = (col2 >> 8 & 0xFF) / 255.0F;
        float f7 = (col2 & 0xFF) / 255.0F;

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);

        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);

        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }
    public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0f / textureWidth;
        float f1 = 1.0f / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0).tex(u * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0).tex((u + width) * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0).tex((u + width) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }
    public static void drawImage(ResourceLocation res, float x, float y, float width, float height, int color) {
        GL11.glDisable((int) 2929);
        GL11.glEnable((int) 3042);
        GL11.glDepthMask((boolean) false);
        GL14.glBlendFuncSeparate((int) 770, (int) 771, (int) 1, (int) 0);
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int alpha = color >> 24 & 0xFF;
        GL11.glColor4f((float) ((float) red / 255.0f), (float) ((float) green / 255.0f), (float) ((float) blue / 255.0f), (float) ((float) alpha / 255.0f));
        mc.getTextureManager().bindTexture(res);
        RenderUtil.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDepthMask((boolean) true);
        GL11.glDisable((int) 3042);
        GL11.glEnable((int) 2929);
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawRect(float x, float y, float x1, float y1, int color) {
        enableGL2D();
        glColor(color);
        drawRect(x, y, x1, y1);
        disableGL2D();
    }
    public static void drawRect(float x, float y, float x1, float y1) {
        GL11.glBegin(7);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    public static void enableGL2D() {
        glDisable(2929);
        glEnable(3042);
        glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        glEnable(3553);
        glDisable(3042);
        glEnable(2929);
        glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static Color reAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569F * (float) c.getRed();
        float g = 0.003921569F * (float) c.getGreen();
        float b = 0.003921569F * (float) c.getBlue();
        return (new Color(r, g, b, alpha)).getRGB();
    }

    public static void color(int color) {
        GL11.glColor4ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF), (byte)(color >> 24 & 0xFF));
    }
    public static void drawCircle(Entity entity, float partialTicks, double rad, int color, float alpha) {
        /*Got this from the people i made the Gui for*/
        ticks += .004 * (System.currentTimeMillis() - lastFrame);

        lastFrame = System.currentTimeMillis();

        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        GlStateManager.color(1, 1, 1, 1);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glShadeModel(GL_SMOOTH);
        GlStateManager.disableCull();

        final double x = interpolate(entity.lastTickPosX, entity.posX, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
        final double y = interpolate(entity.lastTickPosY, entity.posY, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY + Math.sin(ticks) + 1;
        final double z = interpolate(entity.lastTickPosZ, entity.posZ, mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;

        glBegin(GL_TRIANGLE_STRIP);

        for (float i = 0; i < (Math.PI * 2); i += (Math.PI * 2) / 64.F) {

            final double vecX = x + rad * Math.cos(i);
            final double vecZ = z + rad * Math.sin(i);

            color(color, 0);

            glVertex3d(vecX, y - Math.sin(ticks + 1) / 2.7f, vecZ);

            color(color, .52f * alpha);


            glVertex3d(vecX, y, vecZ);
        }

        glEnd();


        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(1.5f);
        glBegin(GL_LINE_STRIP);
        GlStateManager.color(1, 1, 1, 1);
        color(color, .5f * alpha);
        for (int i = 0; i <= 180; i++) {
            glVertex3d(x - Math.sin(i * MathHelper.PI2 / 90) * rad, y, z + Math.cos(i * MathHelper.PI2 / 90) * rad);
        }
        glEnd();

        glShadeModel(GL_FLAT);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        GlStateManager.enableCull();
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
        glColor4f(1f, 1f, 1f, 1f);
    }

    public static void drawTracerLine(Entity entity, float width, Color color, float alpha) {
        float ticks = mc.timer.renderPartialTicks;
        glPushMatrix();

        glLoadIdentity();

        mc.entityRenderer.orientCamera(ticks);
        double[] pos = getInterpolatedPos(entity);

        glDisable(GL_DEPTH_TEST);
        GLUtil.setup2DRendering();

        double yPos = pos[1] + entity.height / 2f;
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(width);

        glBegin(GL_LINE_STRIP);
        color(color.getRGB(), alpha);
        glVertex3d(pos[0], yPos, pos[2]);
        glVertex3d(0, mc.thePlayer.getEyeHeight(), 0);
        glEnd();

        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_DEPTH_TEST);

        GLUtil.end2DRendering();

        glPopMatrix();
    }
    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }
    public static double[] getInterpolatedPos(Entity entity) {
        float ticks = mc.timer.renderPartialTicks;
        return new double[]{
                interpolate(entity.lastTickPosX, entity.posX, ticks) - mc.getRenderManager().viewerPosX,
                interpolate(entity.lastTickPosY, entity.posY, ticks) - mc.getRenderManager().viewerPosY,
                interpolate(entity.lastTickPosZ, entity.posZ, ticks) - mc.getRenderManager().viewerPosZ
        };
    }
    public static void enableGlCap(final int cap) {
        setGlCap(cap, true);
    }
    public static void setGlCap(final int cap, final boolean state) {
        glCapMap.put(cap, glGetBoolean(cap));
        setGlState(cap, state);
    }
    public static void setGlState(final int cap, final boolean state) {
        if (state)
            glEnable(cap);
        else
            glDisable(cap);
    }
    public static void disableGlCap(final int... caps) {
        for (final int cap : caps)
            setGlCap(cap, false);
    }
    public static void glColor2(final int red, final int green, final int blue, final int alpha) {
        GlStateManager.color(red / 255F, green / 255F, blue / 255F, alpha / 255F);
    }
    public static void drawBlockBox(final BlockPos blockPos, final Color color, final boolean outline) {
        final RenderManager renderManager = mc.getRenderManager();
        final Timer timer = mc.timer;

        final double x = blockPos.getX() - renderManager.renderPosX;
        final double y = blockPos.getY() - renderManager.renderPosY;
        final double z = blockPos.getZ() - renderManager.renderPosZ;

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);
        final Block block = BlockUtil.getBlock(blockPos);

        if (block != null) {
            final EntityPlayer player = mc.thePlayer;

            final double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) timer.renderPartialTicks;
            final double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) timer.renderPartialTicks;
            final double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) timer.renderPartialTicks;
            axisAlignedBB = block.getSelectedBoundingBox(mc.theWorld, blockPos)
                    .expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D)
                    .offset(-posX, -posY, -posZ);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        enableGlCap(GL_BLEND);
        disableGlCap(GL_TEXTURE_2D, GL_DEPTH_TEST);
        glDepthMask(false);

        glColor2(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() != 255 ? color.getAlpha() : outline ? 26 : 35);
        drawFilledBox(axisAlignedBB);

        if (outline) {
            glLineWidth(1F);
            enableGlCap(GL_LINE_SMOOTH);
            glColor(color);

            drawSelectionBoundingBox(axisAlignedBB);
        }

        GlStateManager.resetColor();
        glDepthMask(true);
        resetCaps();
    }
    public static void resetCaps() {
        glCapMap.forEach(RenderUtil::setGlState);
    }
    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);

        // Lower Rectangle
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();

        // Upper Rectangle
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();

        // Upper Rectangle
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();

        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();

        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();

        tessellator.draw();
    }
    public static void drawFilledBox(final AxisAlignedBB axisAlignedBB) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();

        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();

        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();

        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();

        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();

        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
        worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
        tessellator.draw();
    }

    public void drawPartialCircle(int cx, int cy, float r, float startAngle, float arcAngle, float lineWidth, Color color) {
        GL11.glPushMatrix();
        GLUtil.startBlend();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);

        GL11.glBegin(GL11.GL_LINE_STRIP);
        float endAngle = startAngle + arcAngle;
        for (float angle = startAngle; angle < endAngle; angle += 1.0F) {
            float rad = (float) Math.toRadians(angle);
            float x = (float) (cx + Math.cos(rad) * r);
            float y = (float) (cy + Math.sin(rad) * r);
            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GLUtil.endBlend();
        GL11.glPopMatrix();
    }

    public static int limit(double i) {
        if (i > 255.0) {
            return 255;
        }
        if (i < 0.0) {
            return 0;
        }
        return (int) i;
    }

    public static void scale(Minecraft mc) {
        switch (mc.gameSettings.guiScale) {
            case 0:
                GlStateManager.scale(0.5, 0.5, 0.5);
                break;
            case 1:
                GlStateManager.scale(2, 2, 2);
                break;
            case 3:
                GlStateManager.scale(0.6666666666666667, 0.6666666666666667, 0.6666666666666667);
                break;
        }
    }
    public static void scaleStart(float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.translate(-x, -y, 0);
    }
    public static void scaleEnd() {
        GlStateManager.popMatrix();
    }


    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void drawTexture(int texture, float x, float y, float width, float height, float u, float v, int textureWidth, int textureHeight) {
        float xTexel = 1.0F / textureWidth;
        float yTexel = 1.0F / textureHeight;

        GlStateManager.bindTexture(texture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0).tex(u * xTexel, (v + height) * yTexel).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0).tex((u + width) * xTexel, (v + height) * yTexel).endVertex();
        worldrenderer.pos(x + width, y, 0.0).tex((u + width) * xTexel, v * yTexel).endVertex();
        worldrenderer.pos(x, y, 0.0).tex(u * xTexel, v * yTexel).endVertex();
        tessellator.draw();
    }
}

package com.alan.clients.module.impl.render.targetinfo.utils.tenacity.utils.render;


import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import static com.alan.clients.util.interfaces.InstanceAccess.mc;
import static com.alan.clients.util.math.MathInterpolation.interpolate;
import static org.lwjgl.opengl.GL11.*;

public class ESPUtil {
    private static final Frustum frustum = new Frustum();
    private static final FloatBuffer windPos = BufferUtils.createFloatBuffer(4);
    private static final IntBuffer intBuffer = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer floatBuffer1 = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer floatBuffer2 = GLAllocation.createDirectFloatBuffer(16);

    public static boolean isInView(Entity ent) {
        frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        return frustum.isBoundingBoxInFrustum(ent.getEntityBoundingBox()) || ent.ignoreFrustumCheck;
    }

    public static Vector3f projectOn2D(float x, float y, float z, int scaleFactor) {
        glGetFloat(GL_MODELVIEW_MATRIX, floatBuffer1);
        glGetFloat(GL_PROJECTION_MATRIX, floatBuffer2);
        glGetInteger(GL_VIEWPORT, intBuffer);
        if (GLU.gluProject(x, y, z, floatBuffer1, floatBuffer2, intBuffer, windPos)) {
            return new Vector3f(windPos.get(0) / scaleFactor, (mc.displayHeight - windPos.get(1)) / scaleFactor, windPos.get(2));
        }
        return null;
    }

    public static double[] getInterpolatedPos(Entity entity) {
        float ticks = mc.timer.renderPartialTicks;
        return new double[]{
             interpolate(entity.lastTickPosX, entity.posX, ticks) - mc.getRenderManager().viewerPosX,
               interpolate(entity.lastTickPosY, entity.posY, ticks) - mc.getRenderManager().viewerPosY,
                interpolate(entity.lastTickPosZ, entity.posZ, ticks) - mc.getRenderManager().viewerPosZ
        };
    }

    public static AxisAlignedBB getInterpolatedBoundingBox(Entity entity) {
        final double[] renderingEntityPos = getInterpolatedPos(entity);
        final double entityRenderWidth = entity.width / 1.5;
        return new AxisAlignedBB(renderingEntityPos[0] - entityRenderWidth,
                renderingEntityPos[1], renderingEntityPos[2] - entityRenderWidth, renderingEntityPos[0] + entityRenderWidth,
                renderingEntityPos[1] + entity.height + (entity.isSneaking() ? -0.3 : 0.18), renderingEntityPos[2] + entityRenderWidth).expand(0.15, 0.15, 0.15);
    }




}

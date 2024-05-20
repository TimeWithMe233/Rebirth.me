package com.alan.clients.module.impl.render.esp;

import com.alan.clients.module.impl.render.ESP;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.value.Mode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CsgoESP extends Mode<ESP> {

    public CsgoESP(String name, ESP parent) {
        super(name, parent);
    }

    public final List<Entity> collectedEntities = new ArrayList<>();

    private boolean isValid(Entity entity) {
        if (entity == mc.thePlayer) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }
        if (entity.isInvisible()) {
            return false;
        }
        if (entity instanceof EntityItem) {
            return false;
        }
        if (entity instanceof EntityAnimal) {
            return false;
        }
        return entity instanceof EntityPlayer;
    }

    private void collectEntities() {
        collectedEntities.clear();
        List playerEntities = (List) mc.theWorld.loadedEntityList;
        for (Object playerEntity : playerEntities) {
            Entity entity = (Entity) playerEntity;
            if (!isValid(entity))
                continue;
            collectedEntities.add(entity);
        }
    }


    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        Color color = getTheme().getFirstColor();
        for (Object o : mc.theWorld.loadedEntityList) {
            if (o instanceof EntityPlayer && o != mc.thePlayer) {
                EntityPlayer ent = (EntityPlayer) o;
                double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
                double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
                double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
                x -= 0.275;
                z -= 0.275;
                y += ent.getEyeHeight() - 0.225 - (ent.isSneaking() ? 0.25 : 0.0);
                final double mid = 0.275;
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                final double rotAdd = -0.25 * (Math.abs(ent.rotationPitch) / 90.0f);
                GL11.glTranslated(0.0, rotAdd, 0.0);
                GL11.glTranslated(x + mid, y + mid, z + mid);
                GL11.glRotated(-ent.rotationYaw % 360.0f, 0.0, 1.0, 0.0);
                GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
                GL11.glTranslated(x + mid, y + mid, z + mid);
                GL11.glRotated(ent.rotationPitch, 1.0, 0.0, 0.0);
                GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glColor4f(1, 1, 1, 0.5f);
                RenderUtil.drawBoundingBox(new AxisAlignedBB(x - 0.0025, y - 0.0025, z - 0.0025, x + 0.55 + 0.0025, y + 0.55 + 0.0025, z + 0.55 + 0.0025));
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }
        }
    };
}
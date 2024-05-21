package com.alan.clients.module.impl.render.ui;
import com.alan.clients.module.impl.render.AllGui;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.value.Mode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class Inventory extends Mode<AllGui> {
    private AllGui guiModule;

    public Inventory(String name, AllGui parent) {
          super(name, parent);
    }
    double startY = -12.0;

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {

        if (this.guiModule == null) {
            this.guiModule = this.getModule(AllGui.class);
        }

        Color logoColor = this.getTheme().getFirstColor();

        //RenderUtil.roundedRectangle(guiModule.position.x + 2F, guiModule.position.y + startY + 10,   170F, 62F,0f , new Color(0,0,0,50));
        FontManager.getProductSansRegular(17).drawString("Inventory", 16f, (startY +  20.0F ) -11,logoColor.getRGB());

        GlStateManager.resetColor();
// render item
        RenderHelper.enableGUIStandardItemLighting();
        renderInv(9, 17, 6, 6);
        renderInv(18, 26, 6, 24);
        renderInv(27, 35, 6, 42);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();


    };
    private void renderInv(int slot, int endSlot, int x, int y) {
        int xOffset = x;
        for (int i = slot; i <= endSlot; i++) {
            xOffset += 18;
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack == null) {
                continue;
            }
            mc.getRenderItem().renderItemAndEffectIntoGUI(stack, xOffset - 18, y);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, xOffset - 18, y);
        }
    }
}

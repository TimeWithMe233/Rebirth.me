package net.minecraft.client.renderer;

import com.alan.clients.newevent.CancellableEvent;
import com.alan.clients.newevent.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;

@Getter
@AllArgsConstructor
public class RenderContainerEvent extends CancellableEvent {
    private final GuiContainer container;


}

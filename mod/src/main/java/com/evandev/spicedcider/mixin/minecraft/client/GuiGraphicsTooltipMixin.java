package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.gui.TooltipRendering;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsTooltipMixin {

    @WrapMethod(method = "renderTooltipInternal")
    private void spicedcider$markTooltip(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner positioner, Operation<Void> original) {
        TooltipRendering.enter();
        try {
            original.call(font, components, mouseX, mouseY, positioner);
        } finally {
            TooltipRendering.exit();
        }
    }
}

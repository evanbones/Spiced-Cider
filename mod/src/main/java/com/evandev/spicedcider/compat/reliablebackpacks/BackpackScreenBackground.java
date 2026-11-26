package com.evandev.spicedcider.compat.reliablebackpacks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class BackpackScreenBackground {
    public static void draw(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int width, Operation<Void> blit) {
        blit.call(guiGraphics, texture, x, y, u, v, width, 83);
        blit.call(guiGraphics, texture, x, y + 83, u, v + 83, width, 54);
        blit.call(guiGraphics, texture, x, y + 137, u, v + 137, width, 4);
        blit.call(guiGraphics, texture, x, y + 141, u, v + 83, width, 83);
    }
}

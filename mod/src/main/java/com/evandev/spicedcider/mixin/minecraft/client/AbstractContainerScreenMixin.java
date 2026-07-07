package com.evandev.spicedcider.mixin.minecraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V", at = @At("HEAD"))
    private void onRenderTooltip(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        if (screen.getClass().getName().equals("com.li64.tide.client.gui.screens.AnglingTableScreen")) {
            Slot slot = this.hoveredSlot;
            if (slot != null && slot.getItem().isEmpty() && screen.getMenu().getCarried().isEmpty()) {
                int index = slot.index;
                Component title = null;

                if (index == 1) {
                    title = Component.translatable("tooltip.spicedcider.angling_slot.line.title");
                } else if (index == 2) {
                    title = Component.translatable("tooltip.spicedcider.angling_slot.bobber.title");
                } else if (index == 3) {
                    title = Component.translatable("tooltip.spicedcider.angling_slot.hook.title");
                }

                if (title != null) {
                    guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(title), x, y);
                }
            }
        }
    }
}

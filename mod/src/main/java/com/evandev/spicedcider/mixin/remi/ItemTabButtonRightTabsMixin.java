package com.evandev.spicedcider.mixin.remi;

import com.evandev.remi.feature.creativemodetab.gui.itemtab.ItemTabButton;
import com.evandev.remi.integration.emi.ScreenManager;
import com.evandev.remi.util.GuiGraphicsUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("remi")
@Mixin(value = ItemTabButton.class, remap = false)
public class ItemTabButtonRightTabsMixin {

    @Shadow
    @Final
    private ItemTabButton.ButtonStyle style;

    @ModifyVariable(method = "renderWidget", name = "iconX", at = @At(value = "INVOKE", target = "Lcom/evandev/remi/util/GuiGraphicsUtils;blitSprite(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private int spicedcider$centerRightTabIcon(int iconX) {
        if (this.style != ItemTabButton.ButtonStyle.RIGHT) {
            return iconX;
        }
        EmiScreenManager.SidebarPanel panel = ScreenManager.getTargetCreativeTabPanel();
        return iconX + (panel != null ? panel.theme.verticalPadding / 4 * 2 : 0);
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lcom/evandev/remi/util/GuiGraphicsUtils;blitSprite(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIII)V"))
    private void spicedcider$mirrorRightTab(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original) {
        if (this.style != ItemTabButton.ButtonStyle.RIGHT) {
            original.call(graphics, sprite, x, y, width, height);
            return;
        }
        graphics.pose().pushPose();
        graphics.pose().translate(x * 2 + width, 0.0, 0.0);
        graphics.pose().scale(-1f, 1f, 1f);
        RenderSystem.disableCull();
        original.call(graphics, sprite, x, y, width, height);
        RenderSystem.enableCull();
        graphics.pose().popPose();
    }
}

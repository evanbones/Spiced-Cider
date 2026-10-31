package com.evandev.spicedcider.mixin.sootychimneys;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("sootychimneys")
@Mixin(targets = "io.github.mortuusars.sootychimneys.integration.jei.renderer.ScalableItemStackRenderer", remap = false)
public class ScalableItemStackRendererMixin {

    @WrapOperation(method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    private void spicedcider$keepBelowTooltip(PoseStack pose, float x, float y, float z, Operation<Void> original,
                                              GuiGraphics graphics, ItemStack stack) {
        pose.translate(0.0F, 0.0F, -150.0F * (z - 1.0F));
        original.call(pose, x, y, z);
    }
}

package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModPaleGardenBlocks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiPalePumpkinBlurMixin {
    @Unique
    private static final ResourceLocation SPICEDCIDER$PALE_BLUR = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/misc/pale_pumpkinblur.png");

    @Shadow
    protected abstract void renderTextureOverlay(GuiGraphics guiGraphics, ResourceLocation shaderLocation, float alpha);

    @WrapOperation(method = "renderCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 0))
    private boolean spicedcider$paleBlur(ItemStack stack, Item item, Operation<Boolean> original, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (stack.is(ModPaleGardenBlocks.CARVED_PALE_PUMPKIN_ITEM.get())) {
            this.renderTextureOverlay(guiGraphics, SPICEDCIDER$PALE_BLUR, 1.0F);
            return false;
        }
        return original.call(stack, item);
    }
}

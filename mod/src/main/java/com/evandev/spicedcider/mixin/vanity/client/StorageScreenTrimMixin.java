package com.evandev.spicedcider.mixin.vanity.client;

import com.evandev.spicedcider.SpicedCider;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.vanity.client.screen.StorageScreen;

@IfModLoaded("vanity")
@Mixin(value = StorageScreen.class, remap = false)
public class StorageScreenTrimMixin {

    @Unique
    private static final ResourceLocation spicedcider$BLANK_TRIM = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/gui/blank_trim.png");

    @WrapOperation(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderFakeItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private void spicedcider$blankTrimIcon(GuiGraphics graphics, ItemStack stack, int x, int y, Operation<Void> original) {
        graphics.blit(spicedcider$BLANK_TRIM, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
    }
}

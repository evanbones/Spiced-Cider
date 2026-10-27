package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.renderer.CopperPatina;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HorseArmorLayer.class)
public class HorseArmorLayerCopperMixin {

    @WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Horse;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/AnimalArmorItem;getTexture()Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation spicedcider$agedCopperHorseArmor(AnimalArmorItem item, Operation<ResourceLocation> original, @Local ItemStack stack) {
        return CopperPatina.texture(original.call(item), stack);
    }
}

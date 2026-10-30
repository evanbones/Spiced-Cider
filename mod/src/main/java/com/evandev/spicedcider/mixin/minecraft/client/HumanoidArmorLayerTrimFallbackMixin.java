package com.evandev.spicedcider.mixin.minecraft.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerTrimFallbackMixin {

    @Shadow
    @Final
    private TextureAtlas armorTrimAtlas;

    @WrapMethod(method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V")
    private void spicedcider$skipUnrenderableTrim(Holder<ArmorMaterial> armorMaterial, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ArmorTrim trim, Model model, boolean innerTexture, Operation<Void> original) {
        ResourceLocation texture = innerTexture ? trim.innerTexture(armorMaterial) : trim.outerTexture(armorMaterial);
        if (spicedcider$missing(this.armorTrimAtlas.getSprite(texture))
                && spicedcider$missing(this.armorTrimAtlas.getSprite(spicedcider$standard(armorMaterial, trim, innerTexture)))) {
            return;
        }
        original.call(armorMaterial, poseStack, bufferSource, packedLight, trim, model, innerTexture);
    }

    @WrapOperation(method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;getSprite(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
    private TextureAtlasSprite spicedcider$standardTrimFallback(TextureAtlas atlas, ResourceLocation texture, Operation<TextureAtlasSprite> original,
                                                               @Local(argsOnly = true) Holder<ArmorMaterial> armorMaterial, @Local(argsOnly = true) ArmorTrim trim, @Local(argsOnly = true) boolean innerTexture) {
        TextureAtlasSprite sprite = original.call(atlas, texture);
        if (!spicedcider$missing(sprite)) return sprite;
        return original.call(atlas, spicedcider$standard(armorMaterial, trim, innerTexture));
    }

    @Unique
    private static ResourceLocation spicedcider$standard(Holder<ArmorMaterial> armorMaterial, ArmorTrim trim, boolean innerTexture) {
        TrimMaterial material = trim.material().value();
        String suffix = material.overrideArmorMaterials().getOrDefault(armorMaterial, material.assetName());
        return trim.pattern().value().assetId().withPath(path -> "trims/models/armor/" + path + (innerTexture ? "_leggings_" : "_") + suffix);
    }

    @Unique
    private static boolean spicedcider$missing(TextureAtlasSprite sprite) {
        return sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation());
    }
}

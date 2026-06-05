package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.renderer.TextureCatcher;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Material.class)
public abstract class MaterialMixin {

    @Shadow
    public abstract ResourceLocation texture();

    @Inject(method = "buffer*", at = @At("HEAD"))
    private void onBuffer(CallbackInfoReturnable<?> cir) {
        if (TextureCatcher.isCapturing()) {
            TextureCatcher.addTexture(this.texture());
        }
    }

    @Inject(method = "sprite", at = @At("HEAD"))
    private void onSprite(CallbackInfoReturnable<?> cir) {
        if (TextureCatcher.isCapturing()) {
            TextureCatcher.addTexture(this.texture());
        }
    }
}
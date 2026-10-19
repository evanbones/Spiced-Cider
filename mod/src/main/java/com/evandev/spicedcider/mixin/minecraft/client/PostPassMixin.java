package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.events.PaleGardenPost;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PostPass.class)
public abstract class PostPassMixin {
    @Shadow
    @Final
    private EffectInstance effect;

    @Inject(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EffectInstance;apply()V"))
    private void spicedcider$paleGardenUniforms(float partialTicks, CallbackInfo ci) {
        if (PaleGardenPost.EFFECT.equals(this.effect.getName())) {
            PaleGardenPost.applyUniforms(this.effect);
        }
    }
}

package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.compat.vista.VistaMirrorScheduler;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.client.textures.perspective.MirrorReflectionTexture;
import net.mehvahdjukaar.vista.common.mirror.MirrorBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("vista")
@Mixin(value = MirrorReflectionTexture.class, remap = false)
public class VistaMirrorRenderCostMixin {

    @Inject(method = "renderReflection", at = @At("HEAD"))
    private void spicedcider$beginRenderCost(MirrorBlockEntity mirror, Vec3 eye, CallbackInfo ci) {
        VistaMirrorScheduler.beginRender();
    }

    @Inject(method = "renderReflection", at = @At("RETURN"))
    private void spicedcider$endRenderCost(MirrorBlockEntity mirror, Vec3 eye, CallbackInfo ci) {
        VistaMirrorScheduler.endRender();
    }
}

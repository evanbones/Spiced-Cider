package com.evandev.spicedcider.roxy.mixin;

import com.evandev.spicedcider.roxy.compat.RoxyVoxyRendererReloadCompat;
import com.evandev.spicedcider.roxy.compat.RoxyLodEntityOcclusion;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("iris")
@Mixin(targets = "net.irisshaders.iris.Iris", remap = false)
public abstract class RoxyIrisReloadMixin {
    @Inject(method = "reload", at = @At("HEAD"), require = 0)
    private static void roxy$beginReload(CallbackInfo callbackInfo) {
        RoxyLodEntityOcclusion.reset();
        RoxyVoxyRendererReloadCompat.beginIrisReload();
    }

    @Inject(method = "reload", at = @At("RETURN"), require = 0)
    private static void roxy$finishReload(CallbackInfo callbackInfo) {
        RoxyVoxyRendererReloadCompat.finishIrisReload();
    }
}

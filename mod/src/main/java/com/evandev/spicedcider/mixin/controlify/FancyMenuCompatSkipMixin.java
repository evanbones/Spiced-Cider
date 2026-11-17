package com.evandev.spicedcider.mixin.controlify;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.compatibility.fancymenu.FancyMenuCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("controlify")
@Mixin(FancyMenuCompat.class)
public class FancyMenuCompatSkipMixin {

    @Inject(method = "registerActions", at = @At("HEAD"), cancellable = true, remap = false)
    private static void spicedcider$skipBrokenAction(CallbackInfo ci) {
        ci.cancel();
    }
}

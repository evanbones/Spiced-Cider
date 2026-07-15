package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.mehvahdjukaar.vista.common.mirror.MirrorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MirrorBlock.class, remap = false)
public class VistaMirrorAlwaysConnectMixin {

    @Inject(method = "squareAspectRatio", at = @At("HEAD"), cancellable = true)
    private void spicedcider$forceNonSquare(CallbackInfoReturnable<Boolean> cir) {
        if (SpicedCiderConfig.COMMON.vistaMirrorAlwaysConnect.get()) {
            cir.setReturnValue(false);
        }
    }
}

package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.compat.vista.VistaMirrorScheduler;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.VistaModClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("vista")
@Mixin(value = VistaModClient.class, remap = false)
public class VistaMirrorFramePacingMixin {

    @Inject(method = "onRenderTickEnd", at = @At("TAIL"))
    private static void spicedcider$endMirrorFrame(Minecraft minecraft, CallbackInfo ci) {
        VistaMirrorScheduler.onEndOfFrame();
    }
}

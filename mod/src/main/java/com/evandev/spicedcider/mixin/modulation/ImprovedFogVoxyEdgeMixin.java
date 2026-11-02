package com.evandev.spicedcider.mixin.modulation;

import com.evandev.spicedcider.compat.vista.VistaFeeds;
import com.evandev.spicedcider.compat.voxy.VoxyBridge;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("modulation")
@IfModLoaded("roxy")
@Mixin(targets = "com.evandev.modulation.client.ImprovedFog", remap = false)
public abstract class ImprovedFogVoxyEdgeMixin {

    @Shadow
    private static float renderFogStart;

    @Shadow
    private static float renderFogEnd;

    @Inject(method = "captureBackground", at = @At("TAIL"))
    private static void spicedcider$fadeAtLodEdge(CallbackInfo ci) {
        if (VistaFeeds.rendering()) return;
        float lodDistance = VoxyBridge.lodDistance();
        if (lodDistance <= renderFogEnd) return;
        renderFogStart = lodDistance - Mth.clamp(lodDistance / 10.0F, 4.0F, 64.0F);
        renderFogEnd = lodDistance;
    }
}

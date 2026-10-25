package com.evandev.spicedcider.mixin.cirrus;

import com.evandev.spicedcider.compat.voxy.VoxyFog;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("cirrus")
@IfModLoaded("roxy")
@Mixin(targets = "com.jvn.cirrus.client.CirrusCloudRenderer", remap = false)
public class CirrusTerrainFogMixin {

    @ModifyReturnValue(method = "hasVisibilityLimitingFog", at = @At("RETURN"))
    private static boolean spicedcider$terrainFog(boolean foggy) {
        return foggy || VoxyFog.overridesFog();
    }
}

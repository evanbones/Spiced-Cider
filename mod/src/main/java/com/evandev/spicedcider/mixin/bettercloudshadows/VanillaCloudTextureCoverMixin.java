package com.evandev.spicedcider.mixin.bettercloudshadows;

import com.evandev.spicedcider.weather.client.CloudCoverClient;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@IfModLoaded("better_cloud_shadows")
@Mixin(targets = "com.evandev.better_cloud_shadows.clouds.VanillaCloudTexture", remap = false)
public class VanillaCloudTextureCoverMixin {

    @WrapMethod(method = "alphaAt")
    private static float spicedcider$cloudCoverAlpha(int x, int z, Operation<Float> original) {
        return CloudCoverClient.active() ? CloudCoverClient.alphaAt(x, z) : original.call(x, z);
    }

    @ModifyReturnValue(method = "signature", at = @At("RETURN"))
    private static long spicedcider$cloudCoverSignature(long signature) {
        return signature * 31 + CloudCoverClient.version();
    }
}

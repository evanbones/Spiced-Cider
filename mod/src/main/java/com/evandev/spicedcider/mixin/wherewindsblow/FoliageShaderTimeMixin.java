package com.evandev.spicedcider.mixin.wherewindsblow;

import com.evandev.spicedcider.compat.wherewindsblow.FoliageShaderTime;
import com.jvn.wherewindsblow.client.foliage.AcediumFoliageShaderSource;
import com.jvn.wherewindsblow.client.foliage.SodiumFoliageShaderSource;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("where_winds_blow")
@Mixin(value = {SodiumFoliageShaderSource.class, AcediumFoliageShaderSource.class}, remap = false)
public class FoliageShaderTimeMixin {

    @ModifyReturnValue(method = "patch", at = @At("RETURN"))
    private static String spicedcider$boundTempo(String source) {
        return FoliageShaderTime.bound(source);
    }
}

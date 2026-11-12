package com.evandev.spicedcider.mixin.modulation;

import com.evandev.modulation.client.ImprovedFog;
import com.evandev.spicedcider.client.events.ThickBiomeFog;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("modulation")
@Mixin(value = ImprovedFog.class, remap = false)
public class ImprovedFogThickFogMixin {

    @ModifyReturnValue(method = "isActive", at = @At("RETURN"))
    private static boolean spicedcider$fadeToFogColour(boolean active) {
        return active && !ThickBiomeFog.replacesSkyBlend();
    }
}

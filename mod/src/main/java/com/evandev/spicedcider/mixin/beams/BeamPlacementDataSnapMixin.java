package com.evandev.spicedcider.mixin.beams;

import com.evandev.spicedcider.compat.beams.BeamSnapping;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.syl.beams.attachment.BeamPlacementData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("beams")
@Mixin(value = BeamPlacementData.class, remap = false)
public class BeamPlacementDataSnapMixin {

    @ModifyReturnValue(method = "snapHit", at = @At("RETURN"))
    private static int[] spicedcider$snapToGrid(int[] original) {
        return BeamSnapping.snap(original);
    }
}

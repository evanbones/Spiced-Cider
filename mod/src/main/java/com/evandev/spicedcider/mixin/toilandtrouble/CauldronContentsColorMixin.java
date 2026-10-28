package com.evandev.spicedcider.mixin.toilandtrouble;

import cc.cassian.cauldrons.core.CauldronContents;
import com.evandev.spicedcider.content.brewing.CauldronBrewing;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("toil_and_trouble")
@Mixin(value = CauldronContents.class, remap = false)
public class CauldronContentsColorMixin {

    @ModifyReturnValue(method = "getColor", at = @At("RETURN"))
    private int spicedcider$clearColor(int color) {
        CauldronContents contents = (CauldronContents) (Object) this;
        if (CauldronBrewing.isTainted(contents.potion())) {
            return CauldronBrewing.TAINTED_COLOR;
        }
        if (contents.customColor().isEmpty() && contents.customEffects().isEmpty() && CauldronBrewing.isClear(contents.potion())) {
            return CauldronBrewing.CLEAR_COLOR;
        }
        return color;
    }
}

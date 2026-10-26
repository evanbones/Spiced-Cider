package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.CocoaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CocoaBlock.class)
public class CocoaBlockKapokMixin {

    @ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
    private boolean spicedcider$cocoaGrowsOnCacaoTrees(boolean original) {
        return false;
    }
}

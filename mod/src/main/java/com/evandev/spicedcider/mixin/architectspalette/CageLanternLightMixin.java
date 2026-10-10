package com.evandev.spicedcider.mixin.architectspalette;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("architects_palette")
@Mixin(targets = "architectspalette.content.blocks.CageLanternBlock", remap = false)
public class CageLanternLightMixin {

    @ModifyVariable(method = "getLightValueLit", at = @At("HEAD"), argsOnly = true)
    private static int spicedcider$brighterCageLanterns(int lightValue) {
        return 15;
    }
}

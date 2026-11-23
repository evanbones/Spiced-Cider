package com.evandev.spicedcider.mixin.fancymenu;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("fancymenu")
@Mixin(targets = "de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser", remap = false)
public class PlaceholderParserLengthMixin {
    @ModifyReturnValue(method = "isTooLongToParse", at = @At("RETURN"))
    private static boolean spicedcider$uncapLength(boolean original) {
        return false;
    }
}

package com.evandev.spicedcider.mixin.fancymenu;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("fancymenu")
@Mixin(targets = "de.keksuccino.fancymenu.util.rendering.text.markdown.MarkdownRenderer", remap = false)
public class MarkdownRendererLengthMixin {
    @ModifyExpressionValue(method = "setText", at = @At(value = "CONSTANT", args = "intValue=45000"))
    private int spicedcider$uncapLength(int original) {
        return Integer.MAX_VALUE;
    }
}

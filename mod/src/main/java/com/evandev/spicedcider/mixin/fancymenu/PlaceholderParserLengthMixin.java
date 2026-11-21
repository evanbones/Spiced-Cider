package com.evandev.spicedcider.mixin.fancymenu;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("fancymenu")
@Mixin(targets = "de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser", remap = false)
public class PlaceholderParserLengthMixin {
    @ModifyExpressionValue(method = "replacePlaceholders(Ljava/lang/String;Ljava/util/HashMap;Z)Ljava/lang/String;", at = @At(value = "CONSTANT", args = "intValue=17000"))
    private static int spicedcider$uncapLength(int original) {
        return Integer.MAX_VALUE;
    }

    @ModifyExpressionValue(method = "replacePlaceholders(Ljava/lang/String;Ljava/util/HashMap;Lde/keksuccino/fancymenu/customization/placeholder/PlaceholderParser$ParsingContext;)Ljava/lang/String;", at = @At(value = "CONSTANT", args = "intValue=17000"))
    private static int spicedcider$uncapContextLength(int original) {
        return Integer.MAX_VALUE;
    }
}

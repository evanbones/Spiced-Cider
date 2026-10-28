package com.evandev.spicedcider.mixin.brickandmortar;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("brick_and_mortar")
@Mixin(targets = "com.evandev.brick_and_mortar.client.integration.KilnEmiRecipe", remap = false)
public class KilnEmiRecipeExperienceMixin {

    @WrapOperation(method = "addWidgets", at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/widget/WidgetHolder;addText(Lnet/minecraft/util/FormattedCharSequence;IIIZ)Ldev/emi/emi/api/widget/TextWidget;"))
    private TextWidget spicedcider$hideExperience(WidgetHolder widgets, FormattedCharSequence text, int x, int y, int color, boolean shadow, Operation<TextWidget> original) {
        return null;
    }
}

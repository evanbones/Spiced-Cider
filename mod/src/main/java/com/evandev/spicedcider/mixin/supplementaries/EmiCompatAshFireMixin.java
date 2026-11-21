package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.compat.emi.FireSlotWidget;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@IfModLoaded("supplementaries")
@IfModLoaded("emi")
@Mixin(targets = "net.mehvahdjukaar.supplementaries.integration.EMICompat", remap = false)
public class EmiCompatAshFireMixin {

    @WrapOperation(
            method = "register",
            at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/recipe/EmiWorldInteractionRecipe$Builder;rightInput(Ldev/emi/emi/api/stack/EmiIngredient;ZLjava/util/function/Function;)Ldev/emi/emi/api/recipe/EmiWorldInteractionRecipe$Builder;")
    )
    private EmiWorldInteractionRecipe.Builder spicedcider$fireSlot(EmiWorldInteractionRecipe.Builder builder, EmiIngredient ingredient, boolean catalyst, Function<SlotWidget, SlotWidget> mutator, Operation<EmiWorldInteractionRecipe.Builder> original) {
        if (!ingredient.isEmpty()) return original.call(builder, ingredient, catalyst, mutator);
        return original.call(builder, ingredient, catalyst, (Function<SlotWidget, SlotWidget>) FireSlotWidget::new);
    }
}

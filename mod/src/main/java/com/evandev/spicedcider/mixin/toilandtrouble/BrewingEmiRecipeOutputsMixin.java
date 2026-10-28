package com.evandev.spicedcider.mixin.toilandtrouble;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("toil_and_trouble")
@IfModLoaded("emi")
@Mixin(targets = "cc.cassian.cauldrons.compat.emi.BrewingEmiRecipe", remap = false)
public abstract class BrewingEmiRecipeOutputsMixin extends BasicEmiRecipe {

    @Shadow
    @Final
    private EmiStack resultForDisplay;

    private BrewingEmiRecipeOutputsMixin(EmiRecipeCategory category, ResourceLocation id, int width, int height) {
        super(category, id, width, height);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void spicedcider$outputCauldronContents(CallbackInfo ci) {
        if (!this.resultForDisplay.isEmpty() && !this.outputs.contains(this.resultForDisplay)) {
            this.outputs.add(this.resultForDisplay);
        }
    }
}

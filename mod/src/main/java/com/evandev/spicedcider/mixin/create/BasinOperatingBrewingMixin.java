package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.create.BasinBrewing;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("create")
@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public class BasinOperatingBrewingMixin {

    @WrapOperation(method = "matchBasinRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;match(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"))
    private boolean spicedcider$matchBrew(BasinBlockEntity basin, Recipe<?> recipe, Operation<Boolean> original) {
        if (recipe == BasinBrewing.RECIPE) return BasinBrewing.canBrew(basin);
        return original.call(basin, recipe);
    }

    @WrapOperation(method = "applyBasinRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"))
    private boolean spicedcider$applyBrew(BasinBlockEntity basin, Recipe<?> recipe, Operation<Boolean> original) {
        if (recipe == BasinBrewing.RECIPE) return BasinBrewing.brew(basin);
        return original.call(basin, recipe);
    }
}

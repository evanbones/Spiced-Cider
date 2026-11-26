package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@IfModLoaded("create")
@Mixin(targets = "com.simibubi.create.compat.jei.CreateJEI", remap = false)
public class CrushingCategoryNoMillingMixin {

    @WrapOperation(method = "loadCategories", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/compat/jei/category/CreateRecipeCategory$Builder;addTypedRecipesExcluding(Ljava/util/function/Supplier;Ljava/util/function/Supplier;)Lcom/simibubi/create/compat/jei/category/CreateRecipeCategory$Builder;", ordinal = 0))
    private CreateRecipeCategory.Builder<?> spicedcider$noMillingInCrushing(CreateRecipeCategory.Builder<?> builder, Supplier<?> include, Supplier<?> exclude, Operation<CreateRecipeCategory.Builder<?>> original) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.crushingWheelsSkipMilling, false)) return builder;
        return original.call(builder, include, exclude);
    }
}

package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@IfModLoaded("create")
@Mixin(value = CrushingWheelControllerBlockEntity.class, remap = false)
public class CrushingWheelNoMillingMixin {

    @WrapOperation(method = "findRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", ordinal = 1))
    private <I extends RecipeInput, R> Optional<R> spicedcider$skipMilling(AllRecipeTypes type, I input, Level level, Operation<Optional<R>> original) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.crushingWheelsSkipMilling, false)) return Optional.empty();
        return original.call(type, input, level);
    }
}

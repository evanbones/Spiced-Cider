package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@IfModLoaded("create")
@Mixin(SawBlockEntity.class)
public class SawBlockEntitySawmillMixin {

    @Unique
    private static final ResourceLocation SPICEDCIDER$SAWING = ResourceLocation.fromNamespaceAndPath("woodworks", "sawing");

    @Unique
    private static boolean spicedcider$isSawing(Object recipe) {
        if (!(recipe instanceof SingleItemRecipe single)) return false;
        return SPICEDCIDER$SAWING.equals(BuiltInRegistries.RECIPE_TYPE.getKey(single.getType()));
    }

    @Inject(method = "isSawable", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$keepBlocks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.createSawsKeepBlocks, false)) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "getRecipes", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/recipe/RecipeConditions;isOfType([Lnet/minecraft/world/item/crafting/RecipeType;)Ljava/util/function/Predicate;"))
    private Predicate<RecipeHolder<? extends Recipe<?>>> spicedcider$includeSawmill(RecipeType<?>[] types, Operation<Predicate<RecipeHolder<? extends Recipe<?>>>> original) {
        return original.call((Object) types).or(holder -> spicedcider$isSawing(holder.value()));
    }

    @WrapOperation(method = "applyRecipe", at = @At(value = "CONSTANT", args = "classValue=net/minecraft/world/item/crafting/StonecutterRecipe"))
    private boolean spicedcider$sawmillResults(Object recipe, Operation<Boolean> original) {
        return original.call(recipe) || spicedcider$isSawing(recipe);
    }
}

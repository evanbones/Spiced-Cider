package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.haunting.SoulHaunting;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(CampfireBlockEntity.class)
public class CampfireSoulHauntingMixin {

    @WrapMethod(method = "getCookableRecipe")
    private Optional<RecipeHolder<CampfireCookingRecipe>> spicedcider$hauntOnSoulCampfire(ItemStack stack, Operation<Optional<RecipeHolder<CampfireCookingRecipe>>> original) {
        CampfireBlockEntity campfire = (CampfireBlockEntity) (Object) this;
        Level level = campfire.getLevel();
        if (level != null && SoulHaunting.isSoulCampfire(campfire.getBlockState())) {
            Optional<RecipeHolder<CampfireCookingRecipe>> haunting = SoulHaunting.campfireRecipe(level, stack);
            if (haunting.isPresent()) return haunting;
        }
        return original.call(stack);
    }

    @WrapOperation(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager$CachedCheck;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private static Optional<?> spicedcider$cookHaunting(RecipeManager.CachedCheck<?, ?> check, RecipeInput input, Level level, Operation<Optional<?>> original, @Local(argsOnly = true) BlockState state) {
        if (SoulHaunting.isSoulCampfire(state) && input instanceof SingleRecipeInput single) {
            Optional<RecipeHolder<CampfireCookingRecipe>> haunting = SoulHaunting.campfireRecipe(level, single.item());
            if (haunting.isPresent()) return haunting;
        }
        return original.call(check, input, level);
    }
}

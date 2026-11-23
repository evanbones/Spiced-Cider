package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.create.BottleFilling;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("create")
@Mixin(value = GenericItemFilling.class, remap = false)
public abstract class GenericItemFillingBottleMixin {

    @Shadow
    private static boolean canFillGlassBottleInternally(FluidStack availableFluid) {
        throw new AssertionError();
    }

    @ModifyReturnValue(method = "getRequiredAmountForItem", at = @At("RETURN"))
    private static int spicedcider$fillingRecipeAmount(int original, Level world, ItemStack stack, FluidStack availableFluid) {
        if (original != -1) return original;
        FillingRecipe recipe = BottleFilling.find(world, stack, availableFluid);
        return recipe == null ? original : recipe.getRequiredFluid().amount();
    }

    @Inject(method = "fillItem", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$fillFromRecipe(Level world, int requiredAmount, ItemStack stack, FluidStack availableFluid, CallbackInfoReturnable<ItemStack> cir) {
        if (canFillGlassBottleInternally(availableFluid)) return;
        FillingRecipe recipe = BottleFilling.find(world, stack, availableFluid);
        if (recipe == null) return;
        ItemStack result = BottleFilling.result(recipe);
        availableFluid.shrink(requiredAmount);
        stack.shrink(1);
        cir.setReturnValue(result);
    }
}

package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.util.BundleCapacity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BundleContents.class)
public class BundleContentsStackSizeMixin {
    @WrapOperation(method = "getWeight", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private static int spicedcider$capBundleStackSize(ItemStack stack, Operation<Integer> original) {
        return Math.min(original.call(stack), Item.DEFAULT_MAX_STACK_SIZE);
    }

    @ModifyReturnValue(method = "getWeight", at = @At("RETURN"))
    private static Fraction spicedcider$scaleToCapacity(Fraction weight) {
        int capacity = BundleCapacity.get();
        return capacity == Item.DEFAULT_MAX_STACK_SIZE ? weight : weight.multiplyBy(Fraction.getFraction(Item.DEFAULT_MAX_STACK_SIZE, capacity));
    }
}

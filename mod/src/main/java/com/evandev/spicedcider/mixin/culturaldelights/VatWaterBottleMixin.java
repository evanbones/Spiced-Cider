package com.evandev.spicedcider.mixin.culturaldelights;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("culturaldelights")
@Mixin(targets = "com.baisylia.culturaldelights.block.entity.custom.VatBlockEntity", remap = false)
public class VatWaterBottleMixin {

    @WrapOperation(method = "craftItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasCraftingRemainingItem()Z"))
    private static boolean spicedcider$bottlesHaveRemainder(ItemStack stack, Operation<Boolean> original) {
        return stack.is(Items.POTION) || original.call(stack);
    }

    @WrapOperation(method = "craftItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCraftingRemainingItem()Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack spicedcider$returnBottle(ItemStack stack, Operation<ItemStack> original) {
        return stack.is(Items.POTION) ? new ItemStack(Items.GLASS_BOTTLE) : original.call(stack);
    }
}

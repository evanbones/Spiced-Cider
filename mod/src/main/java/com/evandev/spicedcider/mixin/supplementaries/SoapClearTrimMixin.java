package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.content.smithing.TrimStyling;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("supplementaries")
@Mixin(targets = "net.mehvahdjukaar.supplementaries.common.items.crafting.SoapClearRecipe", remap = false)
public class SoapClearTrimMixin {

    @WrapOperation(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z"))
    private boolean spicedcider$trimIsWashable(ItemStack stack, DataComponentType<?> type, Operation<Boolean> original) {
        return original.call(stack, type) || stack.has(DataComponents.TRIM);
    }

    @WrapOperation(method = "assemble", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z", ordinal = 0))
    private boolean spicedcider$pickTrimmed(ItemStack stack, DataComponentType<?> type, Operation<Boolean> original) {
        return original.call(stack, type) || stack.has(DataComponents.TRIM);
    }

    @ModifyReturnValue(method = "assemble", at = @At("RETURN"))
    private ItemStack spicedcider$washTrim(ItemStack result) {
        return result.isEmpty() ? result : TrimStyling.withoutTrim(result);
    }
}

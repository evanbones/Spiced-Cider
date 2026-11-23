package com.evandev.spicedcider.mixin.snowyspirit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.snowyspirit.common.entity.GingyEntity;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("snowyspirit")
@Mixin(GingyEntity.class)
public abstract class GingyFeedMixin {

    @WrapOperation(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object foodOfEatenLastCookie(ItemStack stack, DataComponentType<?> type, Operation<Object> original) {
        Object value = original.call(stack, type);
        if (value == null && type == DataComponents.FOOD) {
            return ModRegistry.GINGERBREAD_COOKIE.get().components().get(DataComponents.FOOD);
        }
        return value;
    }
}

package com.evandev.spicedcider.mixin.dimensionaltears;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("dimensional_tears")
@Mixin(targets = "com.ordana.dimensional_tears.items.DimensionalTearsBottleItem", remap = false)
public class TearsBottleContainerDrinkMixin {

    @WrapOperation(
            method = "finishUsingItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V")
    )
    private void spicedcider$keepContainerInHand(ServerPlayer player, InteractionHand hand, ItemStack result, Operation<Void> original,
                                                 @Local(argsOnly = true) ItemStack drunk, @Share("bottle") LocalRef<ItemStack> bottle) {
        if (player.getItemInHand(hand) == drunk) {
            original.call(player, hand, result);
        } else if (result != drunk) {
            bottle.set(result);
        }
    }

    @ModifyReturnValue(method = "finishUsingItem", at = @At("RETURN"))
    private ItemStack spicedcider$returnBottle(ItemStack returned, @Share("bottle") LocalRef<ItemStack> bottle) {
        ItemStack diverted = bottle.get();
        return diverted != null ? diverted : returned;
    }
}

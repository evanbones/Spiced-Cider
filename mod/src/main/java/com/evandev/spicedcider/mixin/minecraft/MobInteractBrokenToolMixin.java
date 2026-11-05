package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.util.BrokenItemUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Armadillo.class, Wolf.class})
public class MobInteractBrokenToolMixin {

    @WrapOperation(
            method = "mobInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;canPerformAction(Lnet/neoforged/neoforge/common/ItemAbility;)Z")
    )
    private boolean cider$brokenToolCantAct(ItemStack stack, ItemAbility ability, Operation<Boolean> original) {
        return !BrokenItemUtil.isBroken(stack) && original.call(stack, ability);
    }
}

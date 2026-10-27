package com.evandev.spicedcider.mixin.piglinproliferation;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("piglinproliferation")
@Mixin(targets = "tallestred.piglinproliferation.common.blockentities.FireRingBlockEntity", remap = false)
public class FireRingNoPotionsMixin {

    @WrapMethod(method = "addEffects")
    private boolean spicedcider$noPotions(Player player, InteractionHand hand, ItemStack stack, PotionContents contents, Operation<Boolean> original) {
        return false;
    }
}

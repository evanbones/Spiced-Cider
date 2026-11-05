package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.block.fire.SulfurFumes;
import com.evandev.spicedcider.registry.ModFireBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireSulfurFoodMixin {
    @ModifyArg(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"), index = 4)
    private static ItemStack spicedcider$sulfurFood(ItemStack stack, @Local(argsOnly = true) BlockState state) {
        return state.is(ModFireBlocks.SULFUR_CAMPFIRE.get()) ? SulfurFumes.taint(stack) : stack;
    }
}

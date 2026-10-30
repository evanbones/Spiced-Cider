package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.handler.TreeBarkEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AxeItem.class)
public class AxeItemTreeBarkMixin {

    @WrapOperation(method = "evaluateNewBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getToolModifiedState(Lnet/minecraft/world/item/context/UseOnContext;Lnet/neoforged/neoforge/common/ItemAbility;Z)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 0))
    private BlockState spicedcider$dropBark(BlockState state, UseOnContext context, ItemAbility ability, boolean simulate, Operation<BlockState> original, Level level, BlockPos pos, Player player) {
        BlockState stripped = original.call(state, context, ability, simulate);
        if (stripped != null) TreeBarkEvents.dropBark(level, pos, context.getClickedFace(), state);
        return stripped;
    }
}

package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.handler.TreeBarkEvents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemTreeBarkMixin {

    @ModifyReturnValue(method = "evaluateNewBlockState", at = @At("RETURN"))
    private Optional<BlockState> spicedcider$dropBark(Optional<BlockState> result, Level level, BlockPos pos, Player player, BlockState state, UseOnContext context) {
        if (result.isPresent()) TreeBarkEvents.dropBark(level, pos, context.getClickedFace(), state, player);
        return result;
    }
}

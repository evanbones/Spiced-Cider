package com.evandev.spicedcider.mixin.bountifulfares;

import com.evandev.spicedcider.compat.bountifulfares.JackOStrawRotation;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.hecco.bountifulfares.definition.block.custom.BrownJackOStrawBlock;
import net.hecco.bountifulfares.definition.block.custom.JackOStrawBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("bountifulfares")
@Mixin({JackOStrawBlock.class, BrownJackOStrawBlock.class})
public class JackOStrawRotationMixin {

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void spicedcider$addOffset(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(JackOStrawRotation.OFFSET);
    }

    @ModifyReturnValue(method = "getStateForPlacement", at = @At("RETURN"))
    private BlockState spicedcider$placeRotated(BlockState original, BlockPlaceContext context) {
        return JackOStrawRotation.place(original, context);
    }

    @Inject(method = "setPlacedBy", at = @At("TAIL"))
    private void spicedcider$rotateUpperHalf(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER) return;
        BlockPos above = pos.above();
        BlockState upper = level.getBlockState(above);
        if (upper.is(state.getBlock()) && upper.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER && upper.getValue(JackOStrawRotation.OFFSET) != state.getValue(JackOStrawRotation.OFFSET)) {
            level.setBlock(above, upper.setValue(JackOStrawRotation.OFFSET, state.getValue(JackOStrawRotation.OFFSET)), Block.UPDATE_CLIENTS);
        }
    }
}

package com.evandev.spicedcider.mixin.minecraft.waterlogging;

import com.evandev.spicedcider.content.waterlogging.ExtraWaterlogging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseExtraWaterloggingMixin {

    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void spicedcider$keepWater(Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = (BlockState) (Object) this;
        Block block = state.getBlock();
        if (!ExtraWaterlogging.isExtra(block)) return;
        boolean waterlogged = state.getValue(BlockStateProperties.WATERLOGGED);
        if (waterlogged) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        BlockState result = cir.getReturnValue();
        if (result.is(block)) {
            if (result.getValue(BlockStateProperties.WATERLOGGED) != waterlogged) cir.setReturnValue(result.setValue(BlockStateProperties.WATERLOGGED, waterlogged));
        } else if (waterlogged && result.isAir()) {
            cir.setReturnValue(Blocks.WATER.defaultBlockState());
        }
    }
}

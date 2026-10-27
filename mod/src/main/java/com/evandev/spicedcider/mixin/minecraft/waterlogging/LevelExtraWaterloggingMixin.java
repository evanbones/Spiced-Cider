package com.evandev.spicedcider.mixin.minecraft.waterlogging;

import com.evandev.spicedcider.content.waterlogging.ExtraWaterlogging;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Level.class)
public abstract class LevelExtraWaterloggingMixin {

    @ModifyVariable(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), argsOnly = true)
    private BlockState spicedcider$waterlogIntoWater(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        if (!ExtraWaterlogging.isExtra(state.getBlock())) return state;
        BlockState previous = ((Level) (Object) this).getBlockState(pos);
        if (previous.is(state.getBlock())) return state;
        return state.setValue(BlockStateProperties.WATERLOGGED, previous.getFluidState().isSourceOfType(Fluids.WATER));
    }
}

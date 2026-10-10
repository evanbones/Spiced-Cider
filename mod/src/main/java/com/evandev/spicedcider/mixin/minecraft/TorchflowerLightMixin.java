package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlowerBlock.class)
public class TorchflowerLightMixin {

    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return (Object) this == Blocks.TORCHFLOWER ? 12 : state.getLightEmission();
    }
}

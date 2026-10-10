package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FlowerPotBlock.class)
public abstract class PottedTorchflowerLightMixin {

    @Shadow
    public abstract Block getPotted();

    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return this.getPotted() == Blocks.TORCHFLOWER ? 12 : state.getLightEmission();
    }
}

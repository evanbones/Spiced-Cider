package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.block.fire.SulfurFireBlock;
import com.evandev.spicedcider.registry.ModFireBlocks;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockSulfurMixin {
    @ModifyReturnValue(method = "getState", at = @At("RETURN"))
    private static BlockState spicedcider$sulfurFire(BlockState original, BlockGetter level, BlockPos pos) {
        return SulfurFireBlock.canSurviveOnBlock(level.getBlockState(pos.below())) ? ModFireBlocks.SULFUR_FIRE.get().defaultBlockState() : original;
    }
}

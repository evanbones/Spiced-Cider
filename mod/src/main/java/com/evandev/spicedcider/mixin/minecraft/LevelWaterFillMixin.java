package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.waterlogging.BrokenBlockWaterFill;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Level.class)
public abstract class LevelWaterFillMixin {
    @ModifyVariable(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), argsOnly = true)
    private BlockState spicedcider$fillWithWater(BlockState state, BlockPos pos) {
        return BrokenBlockWaterFill.fill((Level) (Object) this, pos, state);
    }
}

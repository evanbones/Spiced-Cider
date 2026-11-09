package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.compat.nomansland.tap.TapPowerMemory;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.farcr.nomansland.common.block.tap.TapBlock;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("nomansland")
@Mixin(TapBlock.class)
public class TapBlockRedstoneMixin {

    @Inject(method = "neighborChanged", at = @At("TAIL"))
    private void spicedcider$toggleOnPowerChange(BlockState state, Level level, BlockPos pos, Block neighbor, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
        if (level.isClientSide() || !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.tapRedstone, false)) return;
        if (!(level.getBlockEntity(pos) instanceof TapPowerMemory memory)) return;
        boolean powered = level.hasNeighborSignal(pos);
        Boolean previous = memory.spicedcider$powered();
        memory.spicedcider$setPowered(powered);
        if (previous != null && previous != powered) {
            ((TapBlock) (Object) this).toggle(level.getBlockState(pos), level, pos, null);
        }
    }
}

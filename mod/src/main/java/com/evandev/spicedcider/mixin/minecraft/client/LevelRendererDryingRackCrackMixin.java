package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.content.block.TanningRackBlock;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelRenderer.class)
public class LevelRendererDryingRackCrackMixin {
    @Unique
    private static final int spicedcider$MIRROR_BIT = 0x40000000;

    @Shadow
    private ClientLevel level;

    @Unique
    private boolean spicedcider$mirroring;

    @WrapMethod(method = "destroyBlockProgress")
    private void spicedcider$mirrorRackCrack(int breakerId, BlockPos pos, int progress, Operation<Void> original) {
        original.call(breakerId, pos, progress);
        if (spicedcider$mirroring || (breakerId & spicedcider$MIRROR_BIT) != 0) return;
        spicedcider$mirroring = true;
        try {
            int mirrorId = breakerId | spicedcider$MIRROR_BIT;
            BlockPos other = progress >= 0 && progress < 10 ? spicedcider$otherHalf(pos) : null;
            original.call(mirrorId, other != null ? other : pos, other != null ? progress : -1);
        } finally {
            spicedcider$mirroring = false;
        }
    }

    @Unique
    private BlockPos spicedcider$otherHalf(BlockPos pos) {
        if (this.level == null) return null;
        BlockState state = this.level.getBlockState(pos);
        if (!(state.getBlock() instanceof TanningRackBlock)) return null;
        return state.getValue(TanningRackBlock.HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
    }
}

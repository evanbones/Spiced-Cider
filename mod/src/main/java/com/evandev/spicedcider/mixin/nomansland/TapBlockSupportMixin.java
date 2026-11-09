package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.compat.nomansland.tap.TapFaucetBridge;
import com.evandev.spicedcider.registry.ModTags;
import com.farcr.nomansland.common.block.tap.TapBlock;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nomansland")
@IfModLoaded("supplementaries")
@Mixin(TapBlock.class)
public class TapBlockSupportMixin {

    @ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
    private boolean spicedcider$hangFromFluidContainers(boolean survives, BlockState state, LevelReader level, BlockPos pos) {
        if (survives || !TapFaucetBridge.enabled()) return survives;
        return level.getBlockState(TapBlock.getSourcePos(pos, state)).is(ModTags.Blocks.TAP_SUPPORTS);
    }
}

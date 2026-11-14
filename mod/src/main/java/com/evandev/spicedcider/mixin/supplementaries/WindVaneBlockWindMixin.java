package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.interfaces.WindVaneSignal;
import com.evandev.spicedcider.wind.Wind;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.supplementaries.common.block.blocks.WindVaneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("supplementaries")
@Mixin(value = WindVaneBlock.class, remap = false)
public class WindVaneBlockWindMixin {
    @Unique
    private static final float FULL_SIGNAL_STRENGTH = 1.6F;

    @Inject(method = "updatePower", at = @At("HEAD"))
    private static void spicedcider$updateWindSignal(BlockState state, Level level, BlockPos pos, boolean charged, CallbackInfo ci) {
        if (!(level instanceof ServerLevel server) || !(level.getBlockEntity(pos) instanceof WindVaneSignal vane)) return;
        int signal = -1;
        if (Wind.enabled() && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.windVaneSignal, true)) {
            if (charged) signal = 15;
            else if (!Wind.exposed(server, pos)) signal = 0;
            else signal = Mth.clamp(Math.round(Wind.sample(server, pos).strength() / FULL_SIGNAL_STRENGTH * 15.0F), 0, 15);
        }
        if (signal == vane.spicedcider$getWindSignal()) return;
        vane.spicedcider$setWindSignal(signal);
        level.updateNeighborsAt(pos, state.getBlock());
        level.updateNeighborsAt(pos.below(), state.getBlock());
    }

    @ModifyReturnValue(method = "getSignal", at = @At("RETURN"))
    private int spicedcider$windSignal(int original, BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return spicedcider$signalOr(level, pos, original);
    }

    @ModifyReturnValue(method = "getAnalogOutputSignal", at = @At("RETURN"))
    private int spicedcider$windComparatorSignal(int original, BlockState state, Level level, BlockPos pos) {
        return spicedcider$signalOr(level, pos, original);
    }

    @Unique
    private static int spicedcider$signalOr(BlockGetter level, BlockPos pos, int original) {
        if (level.getBlockEntity(pos) instanceof WindVaneSignal vane && vane.spicedcider$getWindSignal() >= 0) return vane.spicedcider$getWindSignal();
        return original;
    }
}

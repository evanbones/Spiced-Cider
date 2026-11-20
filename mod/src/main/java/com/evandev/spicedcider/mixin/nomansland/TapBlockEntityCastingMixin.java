package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.registry.ModTags;
import com.farcr.nomansland.common.block.tap.TapBlock;
import com.farcr.nomansland.common.blockentity.TapBlockEntity;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("nomansland")
@Mixin(value = TapBlockEntity.class, remap = false)
public class TapBlockEntityCastingMixin {
    @WrapMethod(method = "interval")
    private int spicedcider$pourMoltenMetalFaster(ServerLevel level, Operation<Integer> original) {
        int interval = original.call(level);
        TapBlockEntity tap = (TapBlockEntity) (Object) this;
        BlockState state = tap.getBlockState();
        if (!state.hasProperty(TapBlock.FACING)) return interval;
        return level.getBlockState(TapBlock.getSourcePos(tap.getBlockPos(), state)).is(ModTags.Blocks.FAST_POUR_SOURCES)
                ? Math.min(interval, 10) : interval;
    }
}

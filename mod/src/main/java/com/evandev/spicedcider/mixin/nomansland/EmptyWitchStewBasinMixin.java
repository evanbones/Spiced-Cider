package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.farcr.nomansland.common.block.cauldrons.EmptyWitchStewCauldron;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nomansland")
@Mixin(value = EmptyWitchStewCauldron.class, remap = false)
public class EmptyWitchStewBasinMixin {

    @WrapOperation(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean spicedcider$leaveBasin(Level level, BlockPos pos, BlockState state, Operation<Boolean> original) {
        if (state.is(Blocks.CAULDRON) && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.witchStewBasin, false)) {
            Block basin = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("create", "basin"));
            if (basin != Blocks.AIR) return original.call(level, pos, basin.defaultBlockState());
        }
        return original.call(level, pos, state);
    }
}

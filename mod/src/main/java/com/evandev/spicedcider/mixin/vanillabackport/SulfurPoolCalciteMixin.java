package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LakeFeature.class)
public class SulfurPoolCalciteMixin {

    @WrapOperation(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
            )
    )
    private boolean spicedcider$placeCalciteRingForSulfurPool(
            WorldGenLevel level,
            BlockPos pos,
            BlockState state,
            int flags,
            Operation<Boolean> original,
            FeaturePlaceContext<LakeFeature.Configuration> context
    ) {
        if (state.is(ModBlocks.SULFUR.get())) {
            int originY = context.origin().getY();
            if (pos.getY() >= originY - 1 && pos.getY() <= originY) {
                state = Blocks.CALCITE.defaultBlockState();
            }
        }
        return original.call(level, pos, state, flags);
    }
}

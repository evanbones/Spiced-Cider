package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OceanRuinPieces.OceanRuinPiece.class)
public class OceanRuinChestMixin {
    private static final ResourceLocation PRISMARINE_CHEST = ResourceLocation.fromNamespaceAndPath("quark", "prismarine_chest");

    @WrapOperation(method = "handleDataMarker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean spicedcider$prismarineChest(ServerLevelAccessor level, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
        if (state.is(Blocks.CHEST)) {
            BlockState chest = BuiltInRegistries.BLOCK.getOptional(PRISMARINE_CHEST)
                    .map(block -> block.defaultBlockState())
                    .filter(candidate -> candidate.hasProperty(ChestBlock.WATERLOGGED))
                    .map(candidate -> candidate.setValue(ChestBlock.WATERLOGGED, state.getValue(ChestBlock.WATERLOGGED)))
                    .orElse(state);
            return original.call(level, pos, chest, flags);
        }
        return original.call(level, pos, state, flags);
    }
}

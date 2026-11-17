package com.evandev.spicedcider.mixin.connectiblechains.client;

import com.evandev.spicedcider.compat.connectiblechains.client.ChainHangingLights;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("connectiblechains")
@IfModLoaded("polytone")
@Mixin(targets = "net.mehvahdjukaar.polytone.content.shaders.voxel.PaletteGrid", remap = false)
public abstract class PolytoneChainLightMixin {
    @Unique
    private static final String FILL_SECTION = "fillSection(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/core/SectionPos;Lnet/mehvahdjukaar/polytone/content/shaders/voxel/PaletteGrid$BlockEntitySlot;)V";

    @WrapOperation(method = FILL_SECTION, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState spicedcider$chainLightInSection(LevelChunkSection section, int x, int y, int z, Operation<BlockState> original, @Local(argsOnly = true) SectionPos pos) {
        return ChainHangingLights.lightAt(pos.minBlockX() + x, pos.minBlockY() + y, pos.minBlockZ() + z, original.call(section, x, y, z));
    }

    @WrapOperation(method = "updateBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState spicedcider$chainLightAtBlock(Level level, BlockPos pos, Operation<BlockState> original) {
        return ChainHangingLights.lightAt(pos.getX(), pos.getY(), pos.getZ(), original.call(level, pos));
    }
}

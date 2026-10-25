package com.evandev.spicedcider.mixin.offgrid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Cell;
import dev.tazer.offgrid.world.OffgridChunk;
import dev.tazer.offgrid.world.OffgridIndex;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LightChunkGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@IfModLoaded("offgrid")
@IfModLoaded("scalablelux")
@Mixin(targets = "ca.spottedleaf.starlight.common.light.BlockStarLightEngine", remap = false)
public class StarlightOverlayLightMixin {

    @WrapOperation(method = {"checkBlock", "calculateLightValue", "getSources", "lightChunk"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)I"))
    private int spicedcider$overlayEmission(BlockState state, BlockGetter level, BlockPos pos, Operation<Integer> original) {
        int emission = original.call(state, level, pos);
        if (emission >= 15) return emission;
        OffgridIndex index = OffgridIndex.of(level);
        if (index == null || index.isEmpty()) return emission;
        Cell cell = index.getCell(pos.asLong());
        return cell == null ? emission : Math.max(emission, cell.light());
    }

    @ModifyReturnValue(method = "getSources", at = @At("RETURN"))
    private List<BlockPos> spicedcider$overlaySources(List<BlockPos> sources, LightChunkGetter lightAccess, ChunkAccess chunk) {
        OffgridIndex index = OffgridIndex.of(lightAccess.getLevel());
        if (index == null || index.isEmpty()) return sources;
        OffgridChunk data = index.getChunk(chunk.getPos().toLong());
        if (data == null || data.isEmpty()) return sources;
        List<BlockPos> result = null;
        for (Long2ObjectMap.Entry<Cell> entry : data.getCells().long2ObjectEntrySet()) {
            if (entry.getValue().light() <= 0) continue;
            if (result == null) result = new ArrayList<>(sources);
            result.add(BlockPos.of(entry.getLongKey()));
        }
        return result == null ? sources : result;
    }
}

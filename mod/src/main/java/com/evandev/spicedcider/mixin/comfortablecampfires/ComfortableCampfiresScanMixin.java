package com.evandev.spicedcider.mixin.comfortablecampfires;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;
import java.util.stream.Stream;

@IfModLoaded("comfortable_campfires")
@Mixin(targets = "cc.cassian.campfire.ComfortableCampfires", remap = false)
public class ComfortableCampfiresScanMixin {

    @WrapOperation(method = "applyPlayerEffects(Lnet/minecraft/world/entity/player/Player;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockStatesIfLoaded(Lnet/minecraft/world/phys/AABB;)Ljava/util/stream/Stream;"))
    private static Stream<BlockState> spicedcider$onlyLoadedChunks(Level level, AABB box, Operation<Stream<BlockState>> original) {
        return BlockPos.betweenClosedStream(box).map(pos -> {
            LevelChunk chunk = level.getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
            return chunk == null ? null : chunk.getBlockState(pos);
        }).filter(Objects::nonNull);
    }
}

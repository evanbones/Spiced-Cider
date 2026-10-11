package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.item.CaveMapHeights;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItem.class)
public class CaveMapItemMixin {
    @Inject(method = "update", at = @At("HEAD"))
    private void spicedcider$pickCaveHeight(Level level, Entity viewer, MapItemSavedData data, CallbackInfo ci, @Share("caveHeight") LocalIntRef caveHeight) {
        caveHeight.set(CaveMapHeights.heightFor(level, viewer, data));
    }

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;hasCeiling()Z"))
    private boolean spicedcider$ignoreCeiling(DimensionType type, Operation<Boolean> original, @Share("caveHeight") LocalIntRef caveHeight) {
        return caveHeight.get() == CaveMapHeights.TERRAIN && original.call(type);
    }

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"))
    private int spicedcider$useCaveHeight(LevelChunk chunk, Heightmap.Types type, int x, int z, Operation<Integer> original, @Share("caveHeight") LocalIntRef caveHeight) {
        return caveHeight.get() == CaveMapHeights.TERRAIN ? original.call(chunk, type, x, z) : caveHeight.get();
    }
}

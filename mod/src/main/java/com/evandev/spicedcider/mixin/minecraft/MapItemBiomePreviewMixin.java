package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.locate.BiomePreview;
import com.evandev.spicedcider.locate.LocateWorkers;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MapItem.class)
public class MapItemBiomePreviewMixin {

    @WrapMethod(method = "renderBiomePreviewMap")
    private static void spicedcider$parallelPreview(ServerLevel level, ItemStack stack, Operation<Void> original) {
        MapItemSavedData data = MapItem.getSavedData(stack, level);
        if (data == null || level.dimension() != data.dimension) {
            original.call(level, stack);
            return;
        }
        try {
            BiomePreview.begin(level, data.centerX, data.centerZ, data.scale);
        } catch (LocateWorkers.LocateFailure failure) {
            LocateWorkers.logFallback(failure.getCause());
        }
        try {
            original.call(level, stack);
        } finally {
            BiomePreview.end();
        }
    }

    @WrapOperation(method = "renderBiomePreviewMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"))
    private static Holder<Biome> spicedcider$previewBiome(ServerLevel level, BlockPos pos, Operation<Holder<Biome>> original) {
        BiomeManager preview = BiomePreview.active();
        return preview != null ? preview.getBiome(pos) : original.call(level, pos);
    }
}

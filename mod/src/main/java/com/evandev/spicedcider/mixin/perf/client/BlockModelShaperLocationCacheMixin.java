package com.evandev.spicedcider.mixin.perf.client;

import com.evandev.spicedcider.util.ModelLocationCache;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperLocationCacheMixin {
    @WrapMethod(method = "stateToModelLocation(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/ModelResourceLocation;")
    private static ModelResourceLocation spicedcider$memoize(ResourceLocation id, BlockState state, Operation<ModelResourceLocation> original) {
        Map<BlockState, ModelResourceLocation> cache = ModelLocationCache.active();
        if (cache == null) return original.call(id, state);
        ModelResourceLocation location = cache.get(state);
        if (location != null && location.id().equals(id)) return location;
        location = original.call(id, state);
        cache.put(state, location);
        return location;
    }
}

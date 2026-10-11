package com.evandev.spicedcider.mixin.perf.client;

import com.evandev.spicedcider.util.ModelLocationCache;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerLocationCacheMixin {
    @WrapMethod(method = "loadModels")
    private ModelManager.ReloadState spicedcider$cacheStateLocations(ProfilerFiller profiler, Map<ResourceLocation, AtlasSet.StitchResult> atlases, ModelBakery bakery, Operation<ModelManager.ReloadState> original) {
        ModelLocationCache.begin();
        try {
            return original.call(profiler, atlases, bakery);
        } finally {
            ModelLocationCache.end();
        }
    }
}

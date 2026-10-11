package com.evandev.spicedcider.mixin.perf;

import com.evandev.spicedcider.util.ClimateSampleCache;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Climate.Sampler.class)
public class ClimateSamplerMixin {

    @WrapMethod(method = "sample")
    private Climate.TargetPoint spicedcider$reuseRepeatedSample(int x, int y, int z, Operation<Climate.TargetPoint> original) {
        ClimateSampleCache cache = ClimateSampleCache.get();
        Climate.TargetPoint cached = cache.lookup(this, x, y, z);
        if (cached != null) return cached;
        Climate.TargetPoint point = original.call(x, y, z);
        cache.store(this, x, y, z, point);
        return point;
    }
}

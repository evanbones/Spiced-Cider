package com.evandev.spicedcider.compat.betterclouds;

import com.evandev.spicedcider.mixin.betterclouds.ChunkedGeneratorAccessor;
import com.qendolin.betterclouds.BetterClouds;
import com.qendolin.betterclouds.clouds.*;
import com.qendolin.betterclouds.config.Config;
import com.qendolin.betterclouds.config.ConfigManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;

final class BetterCloudsBridge {

    private static Sampler sampler;

    private BetterCloudsBridge() {
    }

    static CloudField field(ClientLevel level, float partialTick) {
        if (!ConfigManager.isInitialized() || !BetterClouds.isEnabled()) return null;

        Renderer renderer = BetterClouds.getCloudsRenderer();
        if (renderer == null) return null;

        Resources resources = renderer.resources();
        if (resources == null) return null;

        ChunkedGenerator generator = resources.generator();
        if (generator == null || !generator.canRender()) return null;

        Config config = generator.config();
        if (config == null || config.spacing <= 0) return null;

        ResourceKey<DimensionType> dimension = level.dimensionTypeRegistration().unwrapKey().orElse(null);
        if (!ConfigManager.instance().enabledDimensions.contains(dimension)) return null;

        DimensionSpecialEffects effects = level.effects();
        float cloudHeight = effects.getCloudHeight();
        if (Float.isNaN(cloudHeight)) return null;

        float cloudiness = (float) (Math.ceil(CloudinessProvider.getCloudiness(level, partialTick) * 100) / 100.0);
        if (cloudiness <= 0) return null;

        sampler = ((ChunkedGeneratorAccessor) generator).spicedcider$getSampler();
        if (sampler == null) return null;

        return new CloudField(
                generator.originX(),
                generator.originZ(),
                config.spacing,
                config.sparsity,
                config.fuzziness,
                config.samplingScale,
                config.sizeXZ,
                config.bottomSparsity,
                cloudiness,
                cloudHeight + config.yOffset,
                config.blockDistance(),
                signatureOf(sampler, config, cloudiness)
        );
    }

    static float coverage(int gridX, int gridZ, CloudField field) {
        Sampler current = sampler;
        if (current == null) return 0;

        if (field.sparsity() > 0
                && Sampler.hashToFloat(current.getSeed(), 'G', gridX, gridZ) < field.sparsity()) {
            return 0;
        }

        int sampleX = Mth.floor(gridX * field.spacing());
        int sampleZ = Mth.floor(gridZ * field.spacing());
        return current.sample(sampleX, sampleZ, field.cloudiness(), field.fuzziness(), field.samplingScale()) > 0 ? 1f : 0f;
    }

    private static long signatureOf(Sampler sampler, Config config, float cloudiness) {
        long hash = sampler.getSeed();
        hash = hash * 31 + System.identityHashCode(sampler);
        hash = hash * 31 + Float.floatToIntBits(config.spacing);
        hash = hash * 31 + Float.floatToIntBits(config.sparsity);
        hash = hash * 31 + Float.floatToIntBits(config.fuzziness);
        hash = hash * 31 + Float.floatToIntBits(config.samplingScale);
        hash = hash * 31 + Float.floatToIntBits(config.bottomSparsity);
        hash = hash * 31 + Float.floatToIntBits(cloudiness);
        return hash;
    }

    static void invalidate() {
        sampler = null;
    }
}

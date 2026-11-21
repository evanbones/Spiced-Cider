package com.evandev.spicedcider.content.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class StrippedDriftwoodFeature extends Feature<NoneFeatureConfiguration> {
    private static final ResourceLocation DRIFTWOOD = ResourceLocation.fromNamespaceAndPath("upgrade_aquatic", "driftwood");
    private static final ResourceLocation STRIPPED_LOG = ResourceLocation.fromNamespaceAndPath("upgrade_aquatic", "stripped_driftwood_log");
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    public StrippedDriftwoodFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    public static Object swap(Object block) {
        if (!ACTIVE.get()) return block;
        Block stripped = BuiltInRegistries.BLOCK.get(STRIPPED_LOG);
        return stripped == Blocks.AIR ? block : stripped;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        Feature<?> driftwood = BuiltInRegistries.FEATURE.get(DRIFTWOOD);
        if (driftwood == null || driftwood == this) return false;
        ACTIVE.set(true);
        try {
            return ((Feature<NoneFeatureConfiguration>) driftwood).place(NoneFeatureConfiguration.INSTANCE, context.level(), context.chunkGenerator(), context.random(), context.origin());
        } finally {
            ACTIVE.set(false);
        }
    }
}

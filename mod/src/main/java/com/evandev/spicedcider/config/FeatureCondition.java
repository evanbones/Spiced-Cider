package com.evandev.spicedcider.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public record FeatureCondition(String feature) implements ICondition {
    public static final MapCodec<FeatureCondition> CODEC = Codec.STRING
            .validate(name -> SpicedCiderConfig.Common.FEATURES.contains(name)
                    ? DataResult.success(name)
                    : DataResult.error(() -> "Unknown Spiced Cider feature " + name))
            .fieldOf("feature")
            .xmap(FeatureCondition::new, FeatureCondition::feature);

    @Override
    public boolean test(@NotNull IContext context) {
        return SpicedCiderConfig.feature(feature);
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}

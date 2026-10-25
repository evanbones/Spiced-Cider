package com.evandev.spicedcider.weather;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.UniformInt;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

public enum CloudCover implements StringRepresentable {
    CLEAR("clear", UniformInt.of(6000, 18000)),
    MOSTLY_CLEAR("mostly_clear", UniformInt.of(6000, 18000)),
    PARTLY_CLOUDY("partly_cloudy", UniformInt.of(12000, 24000)),
    CLOUDY("cloudy", UniformInt.of(6000, 18000));

    private static final IntFunction<CloudCover> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
    public static final StreamCodec<ByteBuf, CloudCover> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    private final String name;
    private final UniformInt duration;

    CloudCover(String name, UniformInt duration) {
        this.name = name;
        this.duration = duration;
    }

    public static CloudCover byId(int id) {
        return BY_ID.apply(id);
    }

    public int sampleDuration(RandomSource random) {
        return duration.sample(random);
    }

    public CloudCover next(RandomSource random) {
        return switch (this) {
            case CLEAR -> MOSTLY_CLEAR;
            case MOSTLY_CLEAR -> random.nextBoolean() ? CLEAR : PARTLY_CLOUDY;
            case PARTLY_CLOUDY -> random.nextBoolean() ? MOSTLY_CLEAR : CLOUDY;
            case CLOUDY -> PARTLY_CLOUDY;
        };
    }

    public float overcast() {
        return this == CLOUDY ? 1.0F : 0.0F;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}

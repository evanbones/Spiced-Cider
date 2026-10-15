package com.evandev.spicedcider.content.worm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class WormHunt {
    public static final Codec<WormHunt> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("time", 0).forGetter(hunt -> hunt.time),
            BlockPos.CODEC.optionalFieldOf("target").forGetter(hunt -> Optional.ofNullable(hunt.target))
    ).apply(instance, (time, target) -> new WormHunt(time, target.orElse(null))));

    public int time;
    @Nullable
    public BlockPos target;
    public boolean looking;
    public int sniffTime;

    public WormHunt() {
        this(0, null);
    }

    private WormHunt(int time, @Nullable BlockPos target) {
        this.time = time;
        this.target = target;
    }

    public boolean isIdle() {
        return this.time == 0 && this.target == null;
    }
}

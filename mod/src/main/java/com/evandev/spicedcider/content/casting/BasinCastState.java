package com.evandev.spicedcider.content.casting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.fluids.FluidStack;

public record BasinCastState(FluidStack fluid, int cooling) {
    public static final BasinCastState EMPTY = new BasinCastState(FluidStack.EMPTY, 0);
    public static final Codec<BasinCastState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(BasinCastState::fluid),
            Codec.INT.fieldOf("cooling").forGetter(BasinCastState::cooling)
    ).apply(instance, BasinCastState::new));

    public boolean isEmpty() {
        return fluid.isEmpty();
    }
}

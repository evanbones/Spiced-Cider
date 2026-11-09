package com.evandev.spicedcider.compat.nomansland.tap;

import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidStack;
import org.jetbrains.annotations.Nullable;

public class TapFluidState {
    int sourceTimer = -1;
    long streamUntil;
    @Nullable
    SoftFluidStack stream;
    float streamLength;

    @Nullable
    public SoftFluidStack stream() {
        return stream;
    }

    public float streamLength() {
        return streamLength;
    }
}

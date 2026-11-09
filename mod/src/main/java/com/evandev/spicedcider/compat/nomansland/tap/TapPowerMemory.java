package com.evandev.spicedcider.compat.nomansland.tap;

import org.jetbrains.annotations.Nullable;

public interface TapPowerMemory {
    @Nullable
    Boolean spicedcider$powered();

    void spicedcider$setPowered(@Nullable Boolean powered);
}

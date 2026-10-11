package com.evandev.spicedcider.voidwater;

import org.jetbrains.annotations.Nullable;

public interface VoidTrailLevel {
    @Nullable
    VoidTrailColumns spicedcider$voidTrails();

    @Nullable
    ServerVoidTrails spicedcider$serverVoidTrails();

    void spicedcider$setVoidTrails(VoidTrailColumns columns, @Nullable ServerVoidTrails server);
}

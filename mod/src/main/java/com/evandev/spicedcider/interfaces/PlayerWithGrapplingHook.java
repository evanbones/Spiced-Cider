package com.evandev.spicedcider.interfaces;

import com.evandev.spicedcider.entities.projectiles.GrapplingHookEntity;
import org.jetbrains.annotations.Nullable;

public interface PlayerWithGrapplingHook {
    @Nullable
    GrapplingHookEntity spicedcider$getHook();

    void spicedcider$setHook(@Nullable GrapplingHookEntity hookEntity);
}

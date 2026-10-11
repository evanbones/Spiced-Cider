package com.evandev.spicedcider.mixin.leaks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import io.github.mortuusars.exposure.ExposureServer;
import io.github.mortuusars.exposure.world.level.storage.ExposureFrameHistory;
import io.github.mortuusars.exposure.world.level.storage.ExposureRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("exposure")
@Mixin(value = ExposureServer.class, remap = false)
public interface ExposureServerAccessor {
    @Accessor("vault")
    static void spicedcider$setVault(ExposureRepository vault) {
        throw new AssertionError();
    }

    @Accessor("frameHistory")
    static void spicedcider$setFrameHistory(ExposureFrameHistory frameHistory) {
        throw new AssertionError();
    }
}

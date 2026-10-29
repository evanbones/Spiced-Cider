package com.evandev.spicedcider.mixin.clutternomore;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.client.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("clutternomore")
@Mixin(value = ClutterNoMoreClient.class, remap = false)
public interface ClutterNoMoreClientAccessor {

    @Accessor("overlay")
    static void spicedcider$setOverlay(ShapeSwitcherOverlay overlay) {
        throw new AssertionError();
    }
}

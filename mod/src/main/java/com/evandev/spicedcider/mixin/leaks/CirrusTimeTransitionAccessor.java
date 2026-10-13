package com.evandev.spicedcider.mixin.leaks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("cirrus")
@Mixin(targets = "com.jvn.cirrus.client.CirrusTimeTransition", remap = false)
public interface CirrusTimeTransitionAccessor {
    @Accessor("trackedLevel")
    static void spicedcider$setTrackedLevel(ClientLevel level) {
        throw new AssertionError();
    }
}

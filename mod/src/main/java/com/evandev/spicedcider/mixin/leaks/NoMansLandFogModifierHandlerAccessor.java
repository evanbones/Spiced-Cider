package com.evandev.spicedcider.mixin.leaks;

import com.farcr.nomansland.client.ambience.FogModifierHandler;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("nomansland")
@Mixin(value = FogModifierHandler.class, remap = false)
public interface NoMansLandFogModifierHandlerAccessor {
    @Accessor("context")
    FogModifierHandler.FogContext spicedcider$getContext();
}

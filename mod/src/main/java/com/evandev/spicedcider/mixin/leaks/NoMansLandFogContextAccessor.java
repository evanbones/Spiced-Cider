package com.evandev.spicedcider.mixin.leaks;

import com.farcr.nomansland.client.ambience.FogModifierHandler;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("nomansland")
@Mixin(value = FogModifierHandler.FogContext.class, remap = false)
public interface NoMansLandFogContextAccessor {
    @Accessor("level")
    void spicedcider$setLevel(ClientLevel level);
}

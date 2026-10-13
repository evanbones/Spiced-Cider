package com.evandev.spicedcider.compat.nomansland;

import com.evandev.spicedcider.mixin.leaks.NoMansLandFogContextAccessor;
import com.evandev.spicedcider.mixin.leaks.NoMansLandFogModifierHandlerAccessor;
import com.farcr.nomansland.client.ambience.AmbienceHandler;

public final class NoMansLandClientLeaks {
    private NoMansLandClientLeaks() {
    }

    public static void releaseClient() {
        Object context = ((NoMansLandFogModifierHandlerAccessor) AmbienceHandler.FOG_MODIFIER_HANDLER).spicedcider$getContext();
        ((NoMansLandFogContextAccessor) context).spicedcider$setLevel(null);
    }
}

package com.evandev.spicedcider.compat.coldsweat;

import com.evandev.spicedcider.mixin.leaks.ColdSweatAnimationManagerAccessor;
import com.evandev.spicedcider.mixin.leaks.ColdSweatEntityHelperAccessor;
import com.evandev.spicedcider.mixin.leaks.ColdSweatWorldHelperAccessor;
import com.momosoftworks.coldsweat.client.renderer.animation.AnimationManager;
import com.momosoftworks.coldsweat.common.capability.handler.EntityTempManager;
import com.momosoftworks.coldsweat.common.capability.handler.ShearableFurManager;

public final class ColdSweatLeaks {
    public static void releaseServer() {
        EntityTempManager.CAP_CACHE.clearServer();
        ShearableFurManager.CAP_CACHE.clearServer();
        EntityTempManager.TEMP_MODIFIER_IMMUNITIES.clear();
        ColdSweatEntityHelperAccessor.spicedcider$entityDataCache().clear();
        ColdSweatWorldHelperAccessor.spicedcider$dummyPlayers().clear();
        ColdSweatWorldHelperAccessor.spicedcider$dummyEntities().clear();
        ColdSweatWorldHelperAccessor.spicedcider$temperatureCache().clear();
        ColdSweatWorldHelperAccessor.spicedcider$insulationCache().clear();
        ColdSweatWorldHelperAccessor.spicedcider$blockEntityDataCache().clear();
    }

    public static void releaseClient() {
        EntityTempManager.CAP_CACHE.clearClient();
        ShearableFurManager.CAP_CACHE.clearClient();
        AnimationManager.ANIMATION_TIMERS.clear();
        ColdSweatAnimationManagerAccessor.spicedcider$animationStates().clear();
    }
}

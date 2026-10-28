package com.evandev.spicedcider.compat.nomansland;

import com.evandev.spicedcider.registry.ModFluids;
import com.farcr.nomansland.common.integration.create.ResinOilEffectHandler;
import com.simibubi.create.api.effect.OpenPipeEffectHandler;

public class NoMansLandCreate {

    public static void registerOpenPipeEffects() {
        OpenPipeEffectHandler.REGISTRY.register(ModFluids.RESIN_OIL.get(), new ResinOilEffectHandler());
    }
}

package com.evandev.spicedcider.compat.advancementplaques;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Map;

public final class PlaqueSounds {

    private static final SoundEvent NETHER = event("ui.toast.nether_complete");

    private static final Map<ResourceLocation, SoundEvent> OVERRIDES = Map.of(
            id("nether/enter_nether"), NETHER
    );

    public static SoundEvent forAdvancement(ResourceLocation advancement, SoundEvent original) {
        return OVERRIDES.getOrDefault(advancement, original);
    }

    private static SoundEvent event(String path) {
        return SoundEvent.createVariableRangeEvent(id(path));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("spicedcider", path);
    }
}

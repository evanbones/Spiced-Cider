package com.evandev.spicedcider.client;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.firevariants.BuzzierBeesFireBlocks;
import net.mehvahdjukaar.amendments.AmendmentsClient;
import net.minecraft.resources.ResourceLocation;

public class AmendmentsSkullCandleTextures {
    public static void register() {
        AmendmentsClient.SKULL_CANDLES_TEXTURES.get().put(BuzzierBeesFireBlocks.sulfurCandle.get(),
                ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/block/skull_candles/sulfur.png"));
        if (BuzzierBeesFireBlocks.glowlightCandle != null) {
            AmendmentsClient.SKULL_CANDLES_TEXTURES.get().put(BuzzierBeesFireBlocks.glowlightCandle.get(),
                    ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/block/skull_candles/glowlight.png"));
        }
    }
}

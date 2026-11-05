package com.evandev.spicedcider.compat.firevariants;

import net.mehvahdjukaar.suppsquared.SuppSquared;

public class SuppSquaredFireBlocks {
    public static void register(boolean glowlight) {
        SupplementariesFireBlocks.holder("gold_candle_holder_sulfur", BuzzierBeesFireBlocks.SULFUR_FLAME, SuppSquared::getGoldenCandleHolderParticleOffsets);
        if (glowlight) {
            SupplementariesFireBlocks.holder("gold_candle_holder_glowlight", BuzzierBeesFireBlocks.GLOWLIGHT_FLAME, SuppSquared::getGoldenCandleHolderParticleOffsets);
        }
    }
}

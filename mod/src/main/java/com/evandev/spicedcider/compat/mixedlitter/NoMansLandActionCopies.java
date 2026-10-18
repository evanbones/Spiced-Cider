package com.evandev.spicedcider.compat.mixedlitter;

import com.farcr.nomansland.common.entity.variant_action.SetAntlerLayer;
import com.farcr.nomansland.common.entity.variant_action.SetPatternLayer;
import dev.tazer.mixed_litter.actions.VariantActionType;

public class NoMansLandActionCopies {
    public static VariantActionType copy(VariantActionType action) {
        if (action instanceof SetPatternLayer pattern) {
            SetPatternLayer copy = new SetPatternLayer();
            copy.texture = pattern.texture;
            copy.babyTexture = pattern.babyTexture;
            return copy;
        }
        if (action instanceof SetAntlerLayer antler) {
            SetAntlerLayer copy = new SetAntlerLayer();
            copy.texture = antler.texture;
            return copy;
        }
        return null;
    }
}

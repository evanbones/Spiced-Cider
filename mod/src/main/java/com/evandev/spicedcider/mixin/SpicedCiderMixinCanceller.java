package com.evandev.spicedcider.mixin;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class SpicedCiderMixinCanceller implements MixinCanceller {

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {

        if ("dev.tazer.clutternomore.mixin.compat.emi.ItemEmiStackMixin".equals(mixinClassName)) {
            return true;
        }

        return false;
    }
}
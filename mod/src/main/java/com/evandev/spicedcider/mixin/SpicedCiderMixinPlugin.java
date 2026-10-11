package com.evandev.spicedcider.mixin;

import com.evandev.spicedcider.config.LogReconfiguration;
import com.moulberry.mixinconstraints.ConstraintsMixinPlugin;

public class SpicedCiderMixinPlugin extends ConstraintsMixinPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        super.onLoad(mixinPackage);
        LogReconfiguration.run();
    }
}

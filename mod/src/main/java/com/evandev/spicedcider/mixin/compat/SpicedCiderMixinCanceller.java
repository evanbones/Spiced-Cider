package com.evandev.spicedcider.mixin.compat;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class SpicedCiderMixinCanceller implements MixinCanceller {

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {

        if ("com.github.smallinger.copperagebackport.mixin.client.LightTextureMixin".equals(mixinClassName)) {
            return true;
        }

        if ("mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting.LightTextureMixin".equals(mixinClassName)) {
            return true;
        }

        if ("mod.adrenix.nostalgic.mixin.tweak.candy.missing_texture.MissingTextureAtlasSpriteMixin".equals(mixinClassName)) {
            return true;
        }

        if ("dev.tazer.clutternomore.common.mixin.compat.emi.EMIRenderMixin".equals(mixinClassName)) {
            return true;
        }

        return false;
    }
}
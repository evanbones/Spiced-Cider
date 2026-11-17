package com.evandev.spicedcider.mixin.adaptedmobs;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("adaptedmobs")
@Mixin(targets = "com.cf28.adaptedmobs.common.level.entity.mob.creeper.RocketCreeper", remap = false)
public class RocketCreeperDamageMixin {

    @ModifyReturnValue(method = "getExplosionDamageMultiplier", at = @At("RETURN"))
    private float spicedcider$weakerLeapers(float multiplier) {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.weakerLeapers, false) ? multiplier * 2.0F / 3.0F : multiplier;
    }
}

package com.evandev.spicedcider.mixin.modestmeals;

import com.evandev.modest_meals.effect.ModMobEffects;
import com.evandev.spicedcider.compat.modestmeals.NourishmentDuration;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("modest_meals")
@IfModLoaded("farmersdelight")
@Mixin(targets = "com.evandev.modest_meals.compat.farmers_delight.NourishmentEffectHandler", remap = false)
public class NourishmentDurationMixin {
    @ModifyReturnValue(method = "getEffectToApply", at = @At("RETURN"))
    private static MobEffectInstance spicedcider$shorterNourishment(MobEffectInstance effect) {
        if (!effect.is(ModMobEffects.STAMINA_NOURISHMENT)) return effect;
        int percent = NourishmentDuration.percent();
        return percent == 100 ? effect : NourishmentDuration.scale(effect, percent);
    }
}

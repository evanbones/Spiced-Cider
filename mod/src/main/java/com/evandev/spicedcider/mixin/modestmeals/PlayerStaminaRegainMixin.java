package com.evandev.spicedcider.mixin.modestmeals;

import com.evandev.spicedcider.compat.personality.PersonalitySitting;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModMobEffects;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("modest_meals")
@Mixin(targets = "com.evandev.modest_meals.stamina.PlayerStamina", remap = false)
public class PlayerStaminaRegainMixin {
    @Shadow
    @Final
    public Player player;

    @ModifyReturnValue(method = "regainRate", at = @At("RETURN"))
    private float spicedcider$fasterRegain(float rate) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.sittingStaminaRegen, false) && PersonalitySitting.isSittingOrSeated(this.player)) {
            rate *= 2.0F;
        }
        MobEffectInstance invigorated = this.player.getEffect(ModMobEffects.INVIGORATED);
        if (invigorated != null) {
            rate *= 2.0F + invigorated.getAmplifier();
        }
        return rate;
    }
}

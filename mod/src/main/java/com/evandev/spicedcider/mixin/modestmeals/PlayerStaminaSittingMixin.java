package com.evandev.spicedcider.mixin.modestmeals;

import com.evandev.spicedcider.compat.personality.PersonalitySitting;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("modest_meals")
@Mixin(targets = "com.evandev.modest_meals.stamina.PlayerStamina", remap = false)
public class PlayerStaminaSittingMixin {
    @Shadow
    @Final
    public Player player;

    @ModifyReturnValue(method = "regainRate", at = @At("RETURN"))
    private float spicedcider$fasterWhileSitting(float rate) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.sittingStaminaRegen, false) && PersonalitySitting.isSitting(this.player)) {
            return rate * 2.0F;
        }
        return rate;
    }
}

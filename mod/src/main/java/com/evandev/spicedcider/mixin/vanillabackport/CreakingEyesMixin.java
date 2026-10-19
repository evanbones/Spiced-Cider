package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.common.level.entities.mob.monster.creaking.Creaking;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("vanillabackport")
@Mixin(Creaking.class)
public abstract class CreakingEyesMixin {
    @Shadow
    public abstract boolean isTearingDown();

    @ModifyReturnValue(method = "shouldEyesGlow", at = @At("RETURN"))
    private boolean spicedcider$alwaysGlow(boolean original) {
        return this.isTearingDown() ? original : true;
    }
}

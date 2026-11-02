package com.evandev.spicedcider.mixin.wherewindsblow;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.jvn.wherewindsblow.block.GrassBonemealHandler;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("where_winds_blow")
@Mixin(value = GrassBonemealHandler.class, remap = false)
public class GrassBonemealHandlerMixin {

    @Inject(method = "onBonemeal", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$noOvergrownGrass(BonemealEvent event, CallbackInfo ci) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.whereWindsBlowNoBonemealGrass, false)) ci.cancel();
    }
}

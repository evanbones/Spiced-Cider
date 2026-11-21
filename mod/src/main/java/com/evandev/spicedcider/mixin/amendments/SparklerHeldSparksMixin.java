package com.evandev.spicedcider.mixin.amendments;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.amendments.client.renderers.SparklerRendererExtension;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("amendments")
@Mixin(value = SparklerRendererExtension.class, remap = false)
public abstract class SparklerHeldSparksMixin {
    @Inject(method = "hasFlame", at = @At("HEAD"), cancellable = true)
    private void spicedcider$noHeldSparks(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}

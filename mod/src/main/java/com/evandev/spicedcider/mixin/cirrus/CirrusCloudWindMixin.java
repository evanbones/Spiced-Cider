package com.evandev.spicedcider.mixin.cirrus;

import com.evandev.spicedcider.weather.client.CloudWind;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("cirrus")
@Mixin(targets = "com.jvn.cirrus.client.CirrusCloudRenderer", remap = false)
public class CirrusCloudWindMixin {

    @Inject(method = "sampleX", at = @At("RETURN"), cancellable = true)
    private static void spicedcider$windSampleX(@Coerce Object definition, double cameraSample, double windSample, CallbackInfoReturnable<Double> cir) {
        if (!CloudWind.active()) return;
        double speed = ((CirrusLayerDefinitionInvoker) definition).spicedcider$speed();
        cir.setReturnValue(spicedcider$wrap(cir.getReturnValue() + (CloudWind.travelX() / 12.0 - windSample) * speed));
    }

    @Inject(method = "sampleZ", at = @At("RETURN"), cancellable = true)
    private static void spicedcider$windSampleZ(@Coerce Object definition, double cameraSample, CallbackInfoReturnable<Double> cir) {
        if (!CloudWind.active()) return;
        double speed = ((CirrusLayerDefinitionInvoker) definition).spicedcider$speed();
        cir.setReturnValue(spicedcider$wrap(cir.getReturnValue() + CloudWind.travelZ() / 12.0 * speed));
    }

    private static double spicedcider$wrap(double sample) {
        return sample - Mth.floor(sample / 2048.0) * 2048.0;
    }
}

package com.evandev.spicedcider.mixin.bettercloudshadows;

import com.evandev.spicedcider.weather.client.CloudWind;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("better_cloud_shadows")
@IfModLoaded("cirrus")
@Mixin(targets = "com.evandev.better_cloud_shadows.compat.cirrus.CirrusBridge", remap = false)
public class CirrusBridgeWindMixin {

    @ModifyVariable(method = "layer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static double spicedcider$windTravel(double travel) {
        return CloudWind.active() ? CloudWind.travelX() : travel;
    }

    @ModifyArg(method = "layer", at = @At(value = "INVOKE", target = "Lcom/evandev/better_cloud_shadows/clouds/CloudField$Layer;<init>(DDFFFFJLcom/evandev/better_cloud_shadows/clouds/CloudField$Coverage;)V"), index = 1)
    private static double spicedcider$windOriginZ(double originZ, @Local(argsOnly = true, ordinal = 1) double speed) {
        return CloudWind.active() ? originZ - CloudWind.travelZ() * speed : originZ;
    }
}

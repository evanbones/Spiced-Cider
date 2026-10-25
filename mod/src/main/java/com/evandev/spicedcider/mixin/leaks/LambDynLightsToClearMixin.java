package com.evandev.spicedcider.mixin.leaks;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@IfModLoaded("lambdynlights_runtime")
@Mixin(targets = "dev.lambdaurora.lambdynlights.LambDynLights", remap = false)
public class LambDynLightsToClearMixin {
    @Shadow
    @Final
    private List<?> toClear;

    @Inject(method = "onChangeWorld", at = @At("TAIL"))
    private void spicedcider$dropStaleSources(ClientLevel level, CallbackInfo ci) {
        if (level == null) toClear.clear();
    }
}

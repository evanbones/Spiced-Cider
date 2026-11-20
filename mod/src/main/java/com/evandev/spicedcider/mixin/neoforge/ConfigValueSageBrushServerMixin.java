package com.evandev.spicedcider.mixin.neoforge;

import com.evandev.spicedcider.compat.sagebrush.SageBrushClientConfig;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("sage_brush")
@Mixin(value = ModConfigSpec.ConfigValue.class, remap = false)
public abstract class ConfigValueSageBrushServerMixin<T> {

    @Shadow
    private ModConfigSpec spec;

    @Shadow
    public abstract T getDefault();

    @Inject(method = "getRaw()Ljava/lang/Object;", at = @At("HEAD"), cancellable = true)
    private void spicedcider$defaultSageBrushClientConfigOnServer(CallbackInfoReturnable<T> cir) {
        if (spec != null && !spec.isLoaded() && SageBrushClientConfig.isUnloadedOnServer(spec)) {
            cir.setReturnValue(getDefault());
        }
    }
}

package com.evandev.spicedcider.mixin.manuallabour;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("manual_labour")
@Mixin(targets = "com.evandev.manual_labour.compat.jei.ManualRecipeCategory", remap = false)
public class ManualAssemblyNoCatalystMixin {

    @Inject(method = "registerCatalysts", at = @At("HEAD"), cancellable = true)
    private void spicedcider$noAssemblyCatalyst(CallbackInfo ci) {
        if (getClass().getName().equals("com.evandev.manual_labour.compat.jei.ManualAssemblyCategory")) ci.cancel();
    }
}

package com.evandev.spicedcider.mixin.quark;

import com.evandev.spicedcider.client.screen.VaultScreen;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.base.config.QuarkGeneralConfig;

@IfModLoaded("quark")
@Mixin(value = QuarkGeneralConfig.class, remap = false)
public class QuarkGeneralConfigVaultMixin {

    @ModifyReturnValue(method = "isScreenAllowed", at = @At("RETURN"))
    private static boolean spicedcider$allowVaultScreen(boolean original, Object screen) {
        return original || screen instanceof VaultScreen;
    }
}

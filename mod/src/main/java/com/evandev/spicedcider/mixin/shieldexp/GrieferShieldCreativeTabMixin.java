package com.evandev.spicedcider.mixin.shieldexp;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@IfModLoaded("shieldexp")
@Mixin(targets = "org.infernalstudios.shieldexp.events.CreativeTabEvents", remap = false)
public class GrieferShieldCreativeTabMixin {

    @ModifyArg(method = "onBuildTabContents", at = @At(value = "INVOKE", target = "Lorg/infernalstudios/shieldexp/platform/services/IPlatformHelper;isModLoaded(Ljava/lang/String;)Z"))
    private static String spicedcider$alwaysGriefer(String modId) {
        return "savage_and_ravage".equals(modId) ? "shieldexp" : modId;
    }
}

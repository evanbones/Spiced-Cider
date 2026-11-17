package com.evandev.spicedcider.mixin.vanillabackport;

import com.blackgear.vanillabackport.client.api.modules.waypoints.LocatorBarRenderer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("vanillabackport")
@Mixin(LocatorBarRenderer.class)
public class LocatorBarHideGuiMixin {

    @ModifyReturnValue(method = "shouldSkipRendering", at = @At("RETURN"))
    private boolean spicedcider$hideWithGui(boolean skip) {
        return skip || Minecraft.getInstance().options.hideGui;
    }
}

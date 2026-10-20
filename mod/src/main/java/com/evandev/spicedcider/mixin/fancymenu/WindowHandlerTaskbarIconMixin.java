package com.evandev.spicedcider.mixin.fancymenu;

import com.evandev.spicedcider.client.window.TaskbarIcon;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("fancymenu")
@Mixin(targets = "de.keksuccino.fancymenu.util.window.WindowHandler", remap = false)
public class WindowHandlerTaskbarIconMixin {

    @Inject(method = "updateCustomWindowIconWindowsLinux", at = @At("TAIL"))
    private static void spicedcider$syncTaskbarIcon(CallbackInfo ci) {
        TaskbarIcon.sync(Minecraft.getInstance().getWindow().getWindow());
    }
}

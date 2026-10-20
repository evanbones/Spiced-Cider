package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.client.window.TaskbarIcon;
import com.mojang.blaze3d.platform.IconSet;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowTaskbarIconMixin {

    @Shadow
    @Final
    private long window;

    @Inject(method = "setIcon", at = @At("TAIL"))
    private void spicedcider$syncTaskbarIcon(PackResources packResources, IconSet iconSet, CallbackInfo ci) {
        TaskbarIcon.sync(this.window);
    }
}

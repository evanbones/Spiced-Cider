package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.config.LogReconfiguration;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainLogConfigMixin {
    @Inject(method = "main", at = @At("HEAD"), remap = false)
    private static void spicedcider$reapplyLogConfig(String[] args, CallbackInfo ci) {
        LogReconfiguration.run();
    }
}

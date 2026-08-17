package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin {

    @WrapWithCondition(
        method = "removed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/LevelLoadingScreen;triggerImmediateNarration(Z)V"
        )
    )
    private boolean spicedcider$preventImmediateNarration(LevelLoadingScreen screen, boolean immediate) {
        return !SpicedCiderConfig.CLIENT.oldProgressScreen.get();
    }
}

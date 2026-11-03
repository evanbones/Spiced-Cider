package com.evandev.spicedcider.mixin.vista;

import com.evandev.music_tweaks.config.ModConfig;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.VistaModClient;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("vista")
@IfModLoaded("music_tweaks")
@Mixin(value = VistaModClient.class, remap = false)
public class VistaSoundPauseMixin {

    @WrapWithCondition(
            method = "syncSoundPause",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;pause()V")
    )
    private static boolean spicedcider$keepMusicWhenPaused(SoundManager soundManager) {
        return !ModConfig.get().playMusicWhenPaused;
    }
}

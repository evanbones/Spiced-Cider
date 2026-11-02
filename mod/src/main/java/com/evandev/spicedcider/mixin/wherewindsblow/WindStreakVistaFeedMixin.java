package com.evandev.spicedcider.mixin.wherewindsblow;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.jvn.wherewindsblow.client.wind.WindStreakRenderer;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.client.renderer.VistaLevelRenderer;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("where_winds_blow")
@IfModLoaded("vista")
@Mixin(value = WindStreakRenderer.class, remap = false)
public class WindStreakVistaFeedMixin {

    @Inject(method = "onRenderLevelStage", at = @At("HEAD"), cancellable = true)
    private static void spicedcider$skipInVistaFeeds(RenderLevelStageEvent event, CallbackInfo ci) {
        if (VistaLevelRenderer.isRenderingLiveFeed() && SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) {
            ci.cancel();
        }
    }
}

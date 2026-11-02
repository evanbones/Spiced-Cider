package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import me.cortex.voxy.client.core.VoxyRenderSystem;
import me.cortex.voxy.client.core.rendering.Viewport;
import net.mehvahdjukaar.vista.client.renderer.VistaLevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("vista")
@IfModLoaded("voxy")
@Mixin(value = VoxyRenderSystem.class, remap = false)
public abstract class VistaVoxyFeedMixin {

    @Shadow
    public abstract Viewport<?> getViewport();

    @Unique
    private static boolean spicedcider$skipFeed() {
        return VistaLevelRenderer.isRenderingLiveFeed()
                && SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get();
    }

    @Inject(method = "setupViewport", at = @At("HEAD"), cancellable = true)
    private void spicedcider$keepMainViewport(CallbackInfoReturnable<Viewport<?>> cir) {
        if (spicedcider$skipFeed()) {
            cir.setReturnValue(getViewport());
        }
    }

    @Inject(method = "renderOpaque", at = @At("HEAD"), cancellable = true)
    private void spicedcider$skipFeedLods(CallbackInfo ci) {
        if (spicedcider$skipFeed()) {
            ci.cancel();
        }
    }
}

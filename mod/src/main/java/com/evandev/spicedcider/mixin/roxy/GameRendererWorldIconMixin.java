package com.evandev.spicedcider.mixin.roxy;

import com.bawnorton.mixinsquared.TargetHandler;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@IfModLoaded("roxy")
@Mixin(value = GameRenderer.class, priority = 1500)
public class GameRendererWorldIconMixin {

    @TargetHandler(mixin = "net.rasanovum.roxy.mixin.RoxyGameRendererMixin", name = "roxy$skipAutoScreenshot")
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true)
    private void spicedcider$keepWorldIcon(Path path, CallbackInfo original, CallbackInfo ci) {
        ci.cancel();
    }
}

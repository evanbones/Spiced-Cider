package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.compat.vista.VistaMirrorScheduler;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.mehvahdjukaar.vista.client.textures.MirrorReflectionTexture;
import net.mehvahdjukaar.vista.client.textures.MirrorTextureManager;
import net.mehvahdjukaar.vista.common.mirror.MirrorBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MirrorTextureManager.class, remap = false)
public class VistaMirrorThrottleMixin {

    @Inject(method = "processPending", at = @At("HEAD"))
    private static void spicedcider$onProcessPendingStart(CallbackInfo ci) {
        VistaMirrorScheduler.onFrameStart();
    }

    @Redirect(
            method = "processPending",
            at = @At(value = "INVOKE",
                    target = "Lnet/mehvahdjukaar/vista/client/textures/MirrorReflectionTexture;renderReflection(Lnet/mehvahdjukaar/vista/common/mirror/MirrorBlockEntity;Lnet/minecraft/world/phys/Vec3;)V")
    )
    private static void spicedcider$throttledRender(MirrorReflectionTexture text, MirrorBlockEntity mirror, Vec3 eye) {
        if (!SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) {
            text.renderReflection(mirror, eye);
            return;
        }
        VistaMirrorScheduler.maybeRender(text, mirror, eye);
    }
}

package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.mixin.vista.accessor.LevelRendererAccessor;
import com.llamalad7.mixinextras.sugar.Local;
import net.mehvahdjukaar.vista.client.renderer.VistaLevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VistaLevelRenderer.class, remap = false)
public class VistaSharedStateThrashMixin {

    @Inject(
            method = "render(Lnet/mehvahdjukaar/vista/client/textures/PerspectiveTexture;Ljava/lang/Object;Lnet/mehvahdjukaar/vista/client/renderer/SceneCameraSetup;FZLorg/joml/Matrix4f;Lnet/minecraft/world/phys/Vec3;Ljava/lang/Integer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/mehvahdjukaar/vista/client/renderer/LevelRendererFrustumState;apply(Lnet/minecraft/client/renderer/LevelRenderer;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private static void spicedcider$suppressSharedStateThrash(
            CallbackInfo ci, @Local(name = "camera") Camera camera) {
        if (!SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) return;

        Minecraft mc = Minecraft.getInstance();
        LevelRendererAccessor lr = (LevelRendererAccessor) mc.levelRenderer;
        var camPos = camera.getPosition();

        lr.spicedcider$setXTransparentOld(camPos.x);
        lr.spicedcider$setYTransparentOld(camPos.y);
        lr.spicedcider$setZTransparentOld(camPos.z);

        ClientLevel level = mc.level;
        if (level == null) return;
        float cloudHeight = level.effects().getCloudHeight();
        if (Float.isNaN(cloudHeight)) return;

        float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(false);
        double drift = ((float) lr.spicedcider$getTicks() + partialTick) * 0.03F;
        double dx = (camPos.x + drift) / 12.0;
        double dy = cloudHeight - (float) camPos.y + 0.33F;
        double dz = camPos.z / 12.0 + 0.33F;
        dx -= Mth.floor(dx / 2048.0) * 2048;
        dz -= Mth.floor(dz / 2048.0) * 2048;

        lr.spicedcider$setPrevCloudX((int) Math.floor(dx));
        lr.spicedcider$setPrevCloudY((int) Math.floor(dy / 4.0));
        lr.spicedcider$setPrevCloudZ((int) Math.floor(dz));
        lr.spicedcider$setPrevCloudColor(level.getCloudColor(partialTick));
        lr.spicedcider$setPrevCloudsType(mc.options.getCloudsType());
        lr.spicedcider$setGenerateClouds(false);
    }
}

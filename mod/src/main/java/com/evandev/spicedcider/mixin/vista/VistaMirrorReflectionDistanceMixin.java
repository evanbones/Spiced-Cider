package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.mehvahdjukaar.vista.client.textures.MirrorReflectionTexture;
import net.mehvahdjukaar.vista.configs.ClientConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = MirrorReflectionTexture.class, remap = false)
public abstract class VistaMirrorReflectionDistanceMixin {

    @ModifyArg(
            method = "renderReflection",
            at = @At(value = "INVOKE",
                    target = "Lnet/mehvahdjukaar/vista/client/renderer/VistaLevelRenderer;render(Lnet/mehvahdjukaar/vista/client/textures/PerspectiveTexture;Ljava/lang/Object;Lnet/mehvahdjukaar/vista/client/renderer/SceneCameraSetup;FZLorg/joml/Matrix4f;Lnet/minecraft/world/phys/Vec3;Ljava/lang/Integer;)V"),
            index = 7
    )
    private Integer spicedcider$boundReflectionDistance(Integer original) {
        if (!SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) {
            return original;
        }
        int baseDistance = SpicedCiderConfig.CLIENT.vistaMirrorReflectionDistance.get();
        int depth = ((MirrorReflectionTexture) (Object) this).getRecursionDepth();
        if (depth <= 0) {
            return baseDistance;
        }
        double divider = Math.pow(ClientConfigs.MIRROR_RECURSION_DIST_DIVIDER.get(), depth);
        return (int) Math.max(16, baseDistance / divider);
    }
}

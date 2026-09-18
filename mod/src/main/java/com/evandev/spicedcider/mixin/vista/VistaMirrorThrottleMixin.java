package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.compat.vista.VistaMirrorScheduler;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.client.textures.perspective.MirrorReflectionTexture;
import net.mehvahdjukaar.vista.client.textures.perspective.MirrorTextureManager;
import net.mehvahdjukaar.vista.common.mirror.MirrorBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("vista")
@Mixin(value = MirrorTextureManager.class, remap = false)
public class VistaMirrorThrottleMixin {

    @WrapOperation(
            method = "processPending",
            at = @At(value = "INVOKE",
                    target = "Lnet/mehvahdjukaar/vista/client/textures/perspective/MirrorReflectionTexture;renderReflection(Lnet/mehvahdjukaar/vista/common/mirror/MirrorBlockEntity;Lnet/minecraft/world/phys/Vec3;)V")
    )
    private static void spicedcider$throttledRender(MirrorReflectionTexture text, MirrorBlockEntity mirror, Vec3 eye,
                                                    Operation<Void> original) {
        if (!SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()
                || VistaMirrorScheduler.shouldRender(text, mirror, eye)) {
            original.call(text, mirror, eye);
        }
    }

    @WrapOperation(
            method = "getMirrorTexture(Lnet/mehvahdjukaar/vista/common/mirror/MirrorBlockEntity;Lnet/mehvahdjukaar/moonlight/api/util/math/Vec2i;Lnet/minecraft/world/phys/Vec3;I)Lnet/mehvahdjukaar/vista/client/textures/perspective/MirrorReflectionTexture;",
            at = @At(value = "INVOKE",
                    target = "Lnet/mehvahdjukaar/vista/client/textures/perspective/MirrorReflectionTexture;setUpdateNextTick(Z)V")
    )
    private static void spicedcider$throttledTextureRefresh(MirrorReflectionTexture text, boolean update,
                                                            Operation<Void> original,
                                                            @Local(argsOnly = true) MirrorBlockEntity mirror,
                                                            @Local(argsOnly = true) Vec3 eye) {
        if (!SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()
                || VistaMirrorScheduler.shouldRender(text, mirror, eye)) {
            original.call(text, update);
        }
    }
}

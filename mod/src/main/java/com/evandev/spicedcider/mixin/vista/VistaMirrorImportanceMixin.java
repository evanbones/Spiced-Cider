package com.evandev.spicedcider.mixin.vista;

import com.evandev.spicedcider.compat.vista.VistaMirrorImportance;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.vista.client.AdaptiveUpdateScheduler;
import net.mehvahdjukaar.vista.client.textures.perspective.MirrorReflectionTexture;
import net.mehvahdjukaar.vista.common.mirror.MirrorBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("vista")
@Mixin(value = MirrorReflectionTexture.class, remap = false)
public abstract class VistaMirrorImportanceMixin {

    @Unique
    private double spicedcider$credit;

    @WrapOperation(method = "maybeRenderReflection", at = @At(value = "INVOKE", target = "Lnet/mehvahdjukaar/vista/client/AdaptiveUpdateScheduler;runIfShouldUpdate(Ljava/lang/Object;Ljava/lang/Runnable;)V"))
    private void spicedcider$weightMoving(AdaptiveUpdateScheduler<Object> scheduler, Object id, Runnable render, Operation<Void> original,
                                          @Local(argsOnly = true) MirrorBlockEntity mirror, @Local(argsOnly = true) Vec3 eye) {
        original.call(scheduler, id, spicedcider$weighted(render, mirror, eye));
    }

    @WrapOperation(method = "maybeRenderReflection", at = @At(value = "INVOKE", target = "Lnet/mehvahdjukaar/vista/client/AdaptiveUpdateScheduler;runIfShouldUpdate(Ljava/lang/Object;DLjava/lang/Runnable;)V"))
    private void spicedcider$weightIdle(AdaptiveUpdateScheduler<Object> scheduler, Object id, double maxFps, Runnable render, Operation<Void> original,
                                        @Local(argsOnly = true) MirrorBlockEntity mirror, @Local(argsOnly = true) Vec3 eye) {
        original.call(scheduler, id, maxFps, spicedcider$weighted(render, mirror, eye));
    }

    @Unique
    private Runnable spicedcider$weighted(Runnable render, MirrorBlockEntity mirror, Vec3 eye) {
        MirrorReflectionTexture texture = (MirrorReflectionTexture) (Object) this;
        if (!texture.hasRenderedOnce() || !SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()) {
            return render;
        }
        return () -> {
            spicedcider$credit += VistaMirrorImportance.scale(mirror, eye, texture.getRecursionDepth());
            if (spicedcider$credit < 1.0) return;
            spicedcider$credit -= 1.0;
            render.run();
        };
    }
}

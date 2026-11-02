package com.evandev.spicedcider.mixin.wherewindsblow;

import com.evandev.spicedcider.compat.wherewindsblow.WindRenderFrames;
import com.jvn.wherewindsblow.client.foliage.ResponsiveFoliagePhysics;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("where_winds_blow")
@Mixin(value = ResponsiveFoliagePhysics.class, remap = false)
public class FoliageInteractorFrameMixin {

    @WrapMethod(method = "updateShaderInteractors")
    private static void spicedcider$oncePerFrame(Operation<Void> original) {
        if (WindRenderFrames.claimInteractorUpdate()) original.call();
    }
}

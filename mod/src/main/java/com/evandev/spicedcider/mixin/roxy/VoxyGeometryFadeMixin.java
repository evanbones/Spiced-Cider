package com.evandev.spicedcider.mixin.roxy;

import com.evandev.spicedcider.compat.voxy.VoxyFadeIn;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import me.cortex.voxy.client.core.rendering.building.BuiltSection;
import me.cortex.voxy.common.util.MemoryBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@IfModLoaded("roxy")
@Mixin(targets = "me.cortex.voxy.client.core.rendering.section.geometry.BasicAsyncGeometryManager", remap = false)
public class VoxyGeometryFadeMixin {

    @Inject(method = "uploadReplaceSection", at = @At("HEAD"))
    private void spicedcider$collectFadeColumns(int oldId, BuiltSection section, CallbackInfoReturnable<Integer> cir) {
        MemoryBuffer geometry = section.geometryBuffer;
        if (geometry != null) VoxyFadeIn.collect(section.position, geometry.address, geometry.size);
    }
}

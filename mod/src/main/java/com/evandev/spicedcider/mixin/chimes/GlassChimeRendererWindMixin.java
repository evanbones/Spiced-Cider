package com.evandev.spicedcider.mixin.chimes;

import com.cicada.chimes.block.entity.WindBellBE;
import com.cicada.chimes.client.renderer.GlassBERenderer;
import com.evandev.spicedcider.compat.chimes.ChimesWindClient;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("chimes")
@IfModLoaded("livingwind")
@Mixin(value = GlassBERenderer.class, remap = false)
public class GlassChimeRendererWindMixin {

    @ModifyVariable(method = "render(Lcom/cicada/chimes/block/entity/WindBellBE;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("STORE"), name = "f1")
    private float spicedcider$windPhase(float time, WindBellBE chime, float partialTicks) {
        return ChimesWindClient.phase(partialTicks, chime.getBlockPos());
    }

    @ModifyVariable(method = "render(Lcom/cicada/chimes/block/entity/WindBellBE;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("STORE"), name = "a")
    private float spicedcider$windAmplitude(float value, WindBellBE chime) {
        return value * ChimesWindClient.amplitude(chime.getBlockPos());
    }
}

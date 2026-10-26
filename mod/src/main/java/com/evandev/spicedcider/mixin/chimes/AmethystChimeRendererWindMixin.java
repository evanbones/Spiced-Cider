package com.evandev.spicedcider.mixin.chimes;

import com.cicada.chimes.block.entity.WindChimeBE;
import com.cicada.chimes.client.renderer.AmethystBERenderer;
import com.evandev.spicedcider.compat.chimes.ChimesWindClient;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("chimes")
@IfModLoaded("livingwind")
@Mixin(value = AmethystBERenderer.class, remap = false)
public class AmethystChimeRendererWindMixin {

    @ModifyVariable(method = "render(Lcom/cicada/chimes/block/entity/WindChimeBE$AmethystBE;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("STORE"), name = "f1")
    private float spicedcider$windPhase(float time, WindChimeBE.AmethystBE chime, float partialTicks) {
        return ChimesWindClient.phase(partialTicks, chime.getBlockPos());
    }

    @ModifyVariable(method = "render(Lcom/cicada/chimes/block/entity/WindChimeBE$AmethystBE;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("STORE"), name = "a")
    private float spicedcider$windAmplitude(float value, WindChimeBE.AmethystBE chime) {
        return value * ChimesWindClient.amplitude(chime.getBlockPos());
    }
}

package com.evandev.spicedcider.mixin.sodium;

import com.evandev.spicedcider.compat.sodium.WaterDepthFog;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("sodium")
@Mixin(value = SodiumWorldRenderer.class, remap = false)
public class WaterDepthFogCaptureMixin {

    @Inject(method = "drawChunkLayer", at = @At("HEAD"))
    private void spicedcider$captureSceneDepth(RenderType renderType, ChunkRenderMatrices matrices, double x, double y, double z, CallbackInfo ci) {
        if (renderType == RenderType.translucent()) WaterDepthFog.beforeTranslucent();
    }
}

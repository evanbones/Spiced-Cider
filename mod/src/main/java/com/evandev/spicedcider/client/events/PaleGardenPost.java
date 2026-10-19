package com.evandev.spicedcider.client.events;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.EffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = SpicedCider.MOD_ID, value = Dist.CLIENT)
public class PaleGardenPost {
    public static final String EFFECT = "spicedcider:pale_garden";

    private static final Matrix4f INVERSE_PROJECTION = new Matrix4f();

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            event.getProjectionMatrix().invert(INVERSE_PROJECTION);
        }
    }

    public static void applyUniforms(EffectInstance effect) {
        float[] fogColor = RenderSystem.getShaderFogColor();
        effect.safeGetUniform("FogColor").set(fogColor[0], fogColor[1], fogColor[2]);
        effect.safeGetUniform("FogRange").set(PaleGardenFog.fogStart(), PaleGardenFog.fogEnd());
        effect.safeGetUniform("InvProjMat").set(INVERSE_PROJECTION);
    }
}

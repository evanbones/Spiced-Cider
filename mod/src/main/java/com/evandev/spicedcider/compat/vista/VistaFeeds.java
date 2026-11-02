package com.evandev.spicedcider.compat.vista;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mehvahdjukaar.vista.client.renderer.VistaLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.neoforged.fml.ModList;

public final class VistaFeeds {
    private static final boolean LOADED = ModList.get() != null && ModList.get().isLoaded("vista");

    public static boolean rendering() {
        return LOADED && renderingFeed();
    }

    private static boolean renderingFeed() {
        return VistaLevelRenderer.isRenderingLiveFeed();
    }

    public static boolean mirrorFogActive() {
        return rendering()
                && SpicedCiderConfig.CLIENT_SPEC.isLoaded()
                && SpicedCiderConfig.CLIENT.vistaMirrorPerfFixes.get()
                && SpicedCiderConfig.CLIENT.vistaMirrorFog.get();
    }

    public static void applyMirrorFog(FogRenderer.FogMode mode) {
        if (mode != FogRenderer.FogMode.FOG_TERRAIN || !mirrorFogActive()) return;
        float distance = Minecraft.getInstance().gameRenderer.getRenderDistance();
        float end = Math.max(distance * SpicedCiderConfig.CLIENT.vistaMirrorFogEnd.get() / 100.0F, 8.0F);
        float start = Math.min(distance * SpicedCiderConfig.CLIENT.vistaMirrorFogStart.get() / 100.0F, end - 4.0F);
        RenderSystem.setShaderFogStart(start);
        RenderSystem.setShaderFogEnd(end);
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
    }
}

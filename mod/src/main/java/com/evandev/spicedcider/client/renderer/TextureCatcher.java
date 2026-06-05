package com.evandev.spicedcider.client.renderer;

import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class TextureCatcher {
    private static final Set<ResourceLocation> capturedTextures = new HashSet<>();
    private static boolean capturing = false;

    public static void start() {
        capturing = true;
        capturedTextures.clear();
    }

    public static Set<ResourceLocation> stop() {
        capturing = false;
        return new HashSet<>(capturedTextures);
    }

    public static boolean isCapturing() {
        return capturing;
    }

    public static void addTexture(ResourceLocation texture) {
        if (texture != null) {
            String path = texture.getPath();

            if (path.contains("light_map") ||
                    path.startsWith("atlas/") ||
                    path.startsWith("textures/atlas/") ||
                    path.contains("colormap/")) {
                return;
            }

            capturedTextures.add(texture);
        }
    }
}
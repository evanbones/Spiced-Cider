package com.evandev.spicedcider.compat.bigwater;

import bigwater.BigWater;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public final class BigWaterLodTextures {
    private static final ThreadLocal<Remap> ACTIVE = new ThreadLocal<>();

    private record Remap(TextureAtlasSprite still, TextureAtlasSprite flow, float scale) {
    }

    private BigWaterLodTextures() {
    }

    public static TextureAtlasSprite[] begin(BlockAndTintGetter level, FluidState fluid, TextureAtlasSprite[] sprites) {
        ACTIVE.remove();
        if (!level.getClass().getName().startsWith("me.cortex.voxy.")) return sprites;

        TextureAtlasSprite still = replacement(sprites[0]);
        TextureAtlasSprite flow = replacement(sprites[1]);
        float scale = BigWater.getTextureScale(BuiltInRegistries.FLUID.getKey(fluid.getType()).toString()).getB();
        if (still == sprites[0] && flow == sprites[1] && scale == 1.0F) return sprites;

        TextureAtlasSprite[] replaced = sprites.clone();
        replaced[0] = still;
        replaced[1] = flow;
        ACTIVE.set(new Remap(still, flow, scale));
        return replaced;
    }

    public static void end() {
        ACTIVE.remove();
    }

    @Nullable
    public static TextureAtlasSprite scaledSpriteAt(float u, float v) {
        Remap remap = ACTIVE.get();
        if (remap == null) return null;
        if (contains(remap.still, u, v)) return remap.still;
        if (contains(remap.flow, u, v)) return remap.flow;
        return null;
    }

    public static float scale() {
        Remap remap = ACTIVE.get();
        return remap == null ? 1.0F : remap.scale;
    }

    private static TextureAtlasSprite replacement(TextureAtlasSprite original) {
        TextureAtlasSprite sprite = BigWater.getTexture(original.contents().name().toString());
        return sprite == null ? original : sprite;
    }

    private static boolean contains(TextureAtlasSprite sprite, float u, float v) {
        return u >= sprite.getU0() && u <= sprite.getU1() && v >= sprite.getV0() && v <= sprite.getV1();
    }
}

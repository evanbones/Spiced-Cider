package com.evandev.spicedcider.client.stuckarrows;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;

public record HighlightVertexConsumer(VertexConsumer delegate) implements VertexConsumer {
    private static final float RED = 1.0F;
    private static final float GREEN = 0.9F;
    private static final float BLUE = 0.55F;

    public static MultiBufferSource wrap(MultiBufferSource source) {
        return type -> new HighlightVertexConsumer(source.getBuffer(type));
    }

    @Override
    public @NotNull VertexConsumer addVertex(float x, float y, float z) {
        delegate.addVertex(x, y, z);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
        delegate.setColor(Math.min(255, (int) (red * RED + 40)), Math.min(255, (int) (green * GREEN + 30)), (int) (blue * BLUE), alpha);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv(float u, float v) {
        delegate.setUv(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv1(int u, int v) {
        delegate.setUv1(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv2(int u, int v) {
        delegate.setUv2(LightTexture.FULL_BRIGHT & 0xFFFF, LightTexture.FULL_BRIGHT >> 16 & 0xFFFF);
        return this;
    }

    @Override
    public @NotNull VertexConsumer setNormal(float x, float y, float z) {
        delegate.setNormal(x, y, z);
        return this;
    }
}

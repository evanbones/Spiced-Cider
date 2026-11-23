package com.evandev.spicedcider.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.InventoryMenu;

public class CoolingItemBuffer implements MultiBufferSource {
    private final MultiBufferSource inner;
    private final int alpha;
    private final int red;
    private final int green;
    private final int blue;

    public CoolingItemBuffer(MultiBufferSource inner, int alpha, int heat) {
        this.inner = inner;
        this.alpha = Math.clamp(alpha, 0, 255);
        int clamped = Math.clamp(heat, 0, 255);
        this.red = 255 - clamped * 79 / 255;
        this.green = 255 - clamped * 159 / 255;
        this.blue = 255 - clamped * 223 / 255;
    }

    @Override
    public VertexConsumer getBuffer(RenderType type) {
        if (type.toString().contains("glint")) {
            return this.inner.getBuffer(type);
        }
        RenderType target = this.alpha < 255 ? RenderType.entityTranslucentCull(InventoryMenu.BLOCK_ATLAS) : type;
        return new Tinted(this.inner.getBuffer(target));
    }

    private class Tinted implements VertexConsumer {
        private final VertexConsumer delegate;

        Tinted(VertexConsumer delegate) {
            this.delegate = delegate;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            this.delegate.addVertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            this.delegate.setColor(r * CoolingItemBuffer.this.red / 255, g * CoolingItemBuffer.this.green / 255, b * CoolingItemBuffer.this.blue / 255, a * CoolingItemBuffer.this.alpha / 255);
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            this.delegate.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            this.delegate.setUv1(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            this.delegate.setUv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            this.delegate.setNormal(x, y, z);
            return this;
        }
    }
}

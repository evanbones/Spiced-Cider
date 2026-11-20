package com.evandev.spicedcider.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.createmod.catnip.render.PonderRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;

public record LateTranslucentFluidBuffer(MultiBufferSource delegate) implements MultiBufferSource {

    @Override
    public VertexConsumer getBuffer(RenderType type) {
        if (type != PonderRenderTypes.fluid()) return this.delegate.getBuffer(type);
        return new OverlayFilling(this.delegate.getBuffer(Sheets.translucentCullBlockSheet()));
    }

    private record OverlayFilling(VertexConsumer delegate) implements VertexConsumer {

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            this.delegate.addVertex(x, y, z);
            this.delegate.setOverlay(OverlayTexture.NO_OVERLAY);
            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            this.delegate.setColor(red, green, blue, alpha);
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

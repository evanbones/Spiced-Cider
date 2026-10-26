package com.evandev.spicedcider.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

public final class LeashStyle {
    public static final float SCALE = 3.0F;
    public static final float THICKNESS = 0.025F * SCALE;

    public static VertexConsumer color(VertexConsumer consumer, float red, float alpha, Operation<VertexConsumer> original) {
        return red < 0.45F
                ? original.call(consumer, 0.30980393F, 0.1882353F, 0.101960786F, alpha)
                : original.call(consumer, 0.4392157F, 0.29411766F, 0.16470589F, alpha);
    }
}

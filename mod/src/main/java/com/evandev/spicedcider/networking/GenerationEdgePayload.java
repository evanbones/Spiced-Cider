package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GenerationEdgePayload(float east, float west, float south, float north) implements CustomPacketPayload {
    public static final Type<GenerationEdgePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "generation_edge"));

    public static final StreamCodec<ByteBuf, GenerationEdgePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, GenerationEdgePayload::east,
            ByteBufCodecs.FLOAT, GenerationEdgePayload::west,
            ByteBufCodecs.FLOAT, GenerationEdgePayload::south,
            ByteBufCodecs.FLOAT, GenerationEdgePayload::north,
            GenerationEdgePayload::new);

    public GenerationEdgePayload(float[] distances) {
        this(distances[0], distances[1], distances[2], distances[3]);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

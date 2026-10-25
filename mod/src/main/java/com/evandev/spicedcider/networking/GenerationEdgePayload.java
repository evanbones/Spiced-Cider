package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GenerationEdgePayload(float distance) implements CustomPacketPayload {
    public static final Type<GenerationEdgePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "generation_edge"));

    public static final StreamCodec<ByteBuf, GenerationEdgePayload> CODEC =
            ByteBufCodecs.FLOAT.map(GenerationEdgePayload::new, GenerationEdgePayload::distance);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

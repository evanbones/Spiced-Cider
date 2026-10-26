package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record LodChunkPayload(ResourceKey<Level> dimension, int x, int z, byte[] data) implements CustomPacketPayload {
    public static final Type<LodChunkPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "lod_chunk"));

    public static final StreamCodec<ByteBuf, LodChunkPayload> CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), LodChunkPayload::dimension,
            ByteBufCodecs.VAR_INT, LodChunkPayload::x,
            ByteBufCodecs.VAR_INT, LodChunkPayload::z,
            ByteBufCodecs.BYTE_ARRAY, LodChunkPayload::data,
            LodChunkPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public record LodHelloPayload(int radius, Optional<UUID> store) implements CustomPacketPayload {
    public static final Type<LodHelloPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "lod_hello"));

    public static final StreamCodec<ByteBuf, LodHelloPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, LodHelloPayload::radius,
                    ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), LodHelloPayload::store,
                    LodHelloPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

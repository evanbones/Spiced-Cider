package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record LodHelloPayload(int radius) implements CustomPacketPayload {
    public static final Type<LodHelloPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "lod_hello"));

    public static final StreamCodec<ByteBuf, LodHelloPayload> CODEC =
            ByteBufCodecs.VAR_INT.map(LodHelloPayload::new, LodHelloPayload::radius);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

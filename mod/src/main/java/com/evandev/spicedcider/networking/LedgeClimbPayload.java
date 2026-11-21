package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record LedgeClimbPayload() implements CustomPacketPayload {
    public static final LedgeClimbPayload INSTANCE = new LedgeClimbPayload();
    public static final Type<LedgeClimbPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "ledge_climb"));
    public static final StreamCodec<ByteBuf, LedgeClimbPayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

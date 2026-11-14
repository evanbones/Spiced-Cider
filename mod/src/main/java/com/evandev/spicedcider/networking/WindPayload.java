package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record WindPayload(float heading, float steady, float gustPeak, int gustAge, int gustLength) implements CustomPacketPayload {
    public static final Type<WindPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "wind"));

    public static final StreamCodec<ByteBuf, WindPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, WindPayload::heading,
                    ByteBufCodecs.FLOAT, WindPayload::steady,
                    ByteBufCodecs.FLOAT, WindPayload::gustPeak,
                    ByteBufCodecs.VAR_INT, WindPayload::gustAge,
                    ByteBufCodecs.VAR_INT, WindPayload::gustLength,
                    WindPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

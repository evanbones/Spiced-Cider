package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.weather.CloudCover;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CloudCoverPayload(CloudCover cover, boolean fast) implements CustomPacketPayload {
    public static final Type<CloudCoverPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "cloud_cover"));

    public static final StreamCodec<ByteBuf, CloudCoverPayload> CODEC =
            StreamCodec.composite(
                    CloudCover.STREAM_CODEC, CloudCoverPayload::cover,
                    ByteBufCodecs.BOOL, CloudCoverPayload::fast,
                    CloudCoverPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

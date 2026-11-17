package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PullTamedArrowPayload(int entityId, int arrowIndex) implements CustomPacketPayload {
    public static final Type<PullTamedArrowPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "pull_tamed_arrow"));

    public static final StreamCodec<ByteBuf, PullTamedArrowPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, PullTamedArrowPayload::entityId,
                    ByteBufCodecs.VAR_INT, PullTamedArrowPayload::arrowIndex,
                    PullTamedArrowPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

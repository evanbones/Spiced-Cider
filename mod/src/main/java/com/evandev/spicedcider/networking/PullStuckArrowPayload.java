package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PullStuckArrowPayload(int entityId, int slot, int part) implements CustomPacketPayload {
    public static final Type<PullStuckArrowPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "pull_stuck_arrow"));

    public static final StreamCodec<ByteBuf, PullStuckArrowPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, PullStuckArrowPayload::entityId,
                    ByteBufCodecs.VAR_INT, PullStuckArrowPayload::slot,
                    ByteBufCodecs.VAR_INT, PullStuckArrowPayload::part,
                    PullStuckArrowPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

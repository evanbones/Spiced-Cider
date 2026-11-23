package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CrudeArrowPayload(int entityId) implements CustomPacketPayload {
    public static final Type<CrudeArrowPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "crude_arrow"));

    public static final StreamCodec<ByteBuf, CrudeArrowPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CrudeArrowPayload::entityId,
            CrudeArrowPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

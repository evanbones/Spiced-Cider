package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record GravePositionsPayload(List<GlobalPos> positions) implements CustomPacketPayload {
    public static final Type<GravePositionsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "grave_positions"));

    public static final StreamCodec<ByteBuf, GravePositionsPayload> CODEC =
            GlobalPos.STREAM_CODEC.apply(ByteBufCodecs.list()).map(GravePositionsPayload::new, GravePositionsPayload::positions);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

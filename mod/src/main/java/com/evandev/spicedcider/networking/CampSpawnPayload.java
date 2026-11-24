package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CampSpawnPayload(boolean enabled, boolean hasBed, boolean hasCamp) implements CustomPacketPayload {
    public static final Type<CampSpawnPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "camp_spawn"));

    public static final StreamCodec<ByteBuf, CampSpawnPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, CampSpawnPayload::enabled,
                    ByteBufCodecs.BOOL, CampSpawnPayload::hasBed,
                    ByteBufCodecs.BOOL, CampSpawnPayload::hasCamp,
                    CampSpawnPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

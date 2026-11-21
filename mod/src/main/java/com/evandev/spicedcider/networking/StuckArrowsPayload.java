package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.stuckarrows.StuckArrowSlots;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record StuckArrowsPayload(int entityId, List<StuckArrowSlots.Slot> slots) implements CustomPacketPayload {
    public static final Type<StuckArrowsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "stuck_arrows"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StuckArrowsPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, StuckArrowsPayload::entityId,
                    StuckArrowSlots.Slot.STREAM_CODEC.apply(ByteBufCodecs.list(StuckArrowSlots.MAX_SLOTS)), StuckArrowsPayload::slots,
                    StuckArrowsPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

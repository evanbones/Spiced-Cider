package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record VoidTrailPayload(long[] columns, byte[] lengths) implements CustomPacketPayload {
    public static final Type<VoidTrailPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "void_trails"));

    public static final StreamCodec<RegistryFriendlyByteBuf, VoidTrailPayload> CODEC =
            StreamCodec.of(VoidTrailPayload::write, VoidTrailPayload::read);

    private static void write(RegistryFriendlyByteBuf buf, VoidTrailPayload payload) {
        buf.writeVarInt(payload.columns().length);
        for (int i = 0; i < payload.columns().length; i++) {
            buf.writeLong(payload.columns()[i]);
            buf.writeByte(payload.lengths()[i]);
        }
    }

    private static VoidTrailPayload read(RegistryFriendlyByteBuf buf) {
        int count = buf.readVarInt();
        long[] columns = new long[count];
        byte[] lengths = new byte[count];
        for (int i = 0; i < count; i++) {
            columns[i] = buf.readLong();
            lengths[i] = buf.readByte();
        }
        return new VoidTrailPayload(columns, lengths);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

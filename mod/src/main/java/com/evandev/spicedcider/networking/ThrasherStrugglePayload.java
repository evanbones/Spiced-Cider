package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ThrasherStrugglePayload(float progress) implements CustomPacketPayload {
    public static final Type<ThrasherStrugglePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "thrasher_struggle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ThrasherStrugglePayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.FLOAT, ThrasherStrugglePayload::progress, ThrasherStrugglePayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record LootDisplayPayload(List<Table> tables) implements CustomPacketPayload {
    public static final Type<LootDisplayPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "loot_display"));

    public record Drop(ItemStack stack, int min, int max, List<String> biomes, List<String> structures) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Drop> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, Drop::stack,
                ByteBufCodecs.VAR_INT, Drop::min,
                ByteBufCodecs.VAR_INT, Drop::max,
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), Drop::biomes,
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), Drop::structures,
                Drop::new);
    }

    public record Table(String kind, ResourceLocation id, List<ResourceLocation> inputs, List<Drop> drops) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Table> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, Table::kind,
                ResourceLocation.STREAM_CODEC, Table::id,
                ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), Table::inputs,
                Drop.STREAM_CODEC.apply(ByteBufCodecs.list()), Table::drops,
                Table::new);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, LootDisplayPayload> CODEC = StreamCodec.composite(
            Table.STREAM_CODEC.apply(ByteBufCodecs.list()), LootDisplayPayload::tables,
            LootDisplayPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

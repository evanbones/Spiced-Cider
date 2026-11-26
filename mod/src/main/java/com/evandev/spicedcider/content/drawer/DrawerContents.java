package com.evandev.spicedcider.content.drawer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record DrawerContents(ItemStack item, int count, boolean locked) {

    public static final Codec<DrawerContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("item", ItemStack.EMPTY).forGetter(DrawerContents::item),
            Codec.INT.optionalFieldOf("count", 0).forGetter(DrawerContents::count),
            Codec.BOOL.optionalFieldOf("locked", false).forGetter(DrawerContents::locked)
    ).apply(instance, DrawerContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DrawerContents> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, DrawerContents::item,
            ByteBufCodecs.VAR_INT, DrawerContents::count,
            ByteBufCodecs.BOOL, DrawerContents::locked,
            DrawerContents::new);

    @Override
    public boolean equals(Object other) {
        return other instanceof DrawerContents that && count == that.count && locked == that.locked && ItemStack.matches(item, that.item);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * ItemStack.hashItemAndComponents(item) + count) + (locked ? 1 : 0);
    }
}

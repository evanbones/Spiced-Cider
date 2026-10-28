package com.evandev.spicedcider.networking;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record CauldronStirPayload(BlockPos pos, ItemStack spoon) implements CustomPacketPayload {
    public static final Type<CauldronStirPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "cauldron_stir"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CauldronStirPayload> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CauldronStirPayload::pos,
                    ItemStack.OPTIONAL_STREAM_CODEC, CauldronStirPayload::spoon,
                    CauldronStirPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

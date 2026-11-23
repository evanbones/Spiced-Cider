package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.stuckarrows.StuckArrowSlots;
import com.evandev.spicedcider.content.worm.WormHunt;
import com.evandev.spicedcider.entities.balloon.BalloonTethers;
import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, SpicedCider.MOD_ID);

    public static final Supplier<AttachmentType<Boolean>> WEB_SHOOTING = ATTACHMENT_TYPES.register(
            "web_shooting",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .build()
    );

    public static final Supplier<AttachmentType<WormHunt>> WORM_HUNT = ATTACHMENT_TYPES.register(
            "worm_hunt",
            () -> AttachmentType.builder(WormHunt::new)
                    .serialize(WormHunt.CODEC, hunt -> !hunt.isIdle())
                    .build()
    );

    public static final Supplier<AttachmentType<Boolean>> NO_KNOCKBACK = ATTACHMENT_TYPES.register(
            "no_knockback",
            () -> AttachmentType.builder(() -> false).build()
    );

    public static final Supplier<AttachmentType<BalloonTethers>> BALLOONS = ATTACHMENT_TYPES.register(
            "balloons",
            () -> AttachmentType.builder(BalloonTethers::new)
                    .serialize(BalloonTethers.SERIALIZER)
                    .build()
    );

    public static final Supplier<AttachmentType<StuckArrowSlots>> STUCK_ARROWS = ATTACHMENT_TYPES.register(
            "stuck_arrows",
            () -> AttachmentType.builder(() -> new StuckArrowSlots())
                    .serialize(StuckArrowSlots.CODEC, slots -> !slots.isEmpty())
                    .build()
    );

    public static final Supplier<AttachmentType<Boolean>> CRUDE_ARROW = ATTACHMENT_TYPES.register(
            "crude_arrow",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .build()
    );

    public static final Supplier<AttachmentType<ItemStack>> SHEATHED_BOW = ATTACHMENT_TYPES.register(
            "sheathed_bow",
            () -> AttachmentType.builder(() -> ItemStack.EMPTY)
                    .serialize(ItemStack.OPTIONAL_CODEC, stack -> !stack.isEmpty())
                    .build()
    );

    public static final Supplier<AttachmentType<List<ItemStack>>> SOULBOUND_ITEMS = ATTACHMENT_TYPES.register(
            "soulbound_items",
            () -> AttachmentType.<List<ItemStack>>builder(() -> List.of())
                    .serialize(ItemStack.CODEC.listOf(), list -> !list.isEmpty())
                    .copyOnDeath()
                    .build()
    );
}

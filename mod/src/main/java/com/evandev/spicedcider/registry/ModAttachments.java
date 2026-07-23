package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, SpicedCider.MOD_ID);

    public static final Supplier<AttachmentType<Boolean>> WEB_SHOOTING = ATTACHMENT_TYPES.register(
            "web_shooting",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .build()
    );

    public static final Supplier<AttachmentType<Boolean>> NO_KNOCKBACK = ATTACHMENT_TYPES.register(
            "no_knockback",
            () -> AttachmentType.builder(() -> false).build()
    );
}
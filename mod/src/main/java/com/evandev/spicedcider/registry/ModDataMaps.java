package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ModDataMaps {

    public static final DataMapType<EntityType<?>, SkullDrop> SKULL_DROPS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "skull_drops"),
            Registries.ENTITY_TYPE,
            SkullDrop.CODEC
    ).build();

    @SubscribeEvent
    public static void register(RegisterDataMapTypesEvent event) {
        event.register(SKULL_DROPS);
    }

    public record SkullDrop(Item skull, float chance) {
        public static final Codec<SkullDrop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("skull").forGetter(SkullDrop::skull),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance", 0.1F).forGetter(SkullDrop::chance)
        ).apply(instance, SkullDrop::new));
    }
}

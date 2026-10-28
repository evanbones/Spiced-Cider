package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.Map;
import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class ModDataMaps {

    public static final DataMapType<EntityType<?>, SkullDrop> SKULL_DROPS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "skull_drops"),
            Registries.ENTITY_TYPE,
            SkullDrop.CODEC
    ).build();

    public static final DataMapType<EntityType<?>, SpawnEquipment> SPAWN_EQUIPMENT = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "spawn_equipment"),
            Registries.ENTITY_TYPE,
            SpawnEquipment.CODEC
    ).build();

    public static final DataMapType<Item, BrewingIngredient> BREWING_INGREDIENTS = DataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "brewing_ingredients"),
            Registries.ITEM,
            BrewingIngredient.CODEC
    ).synced(BrewingIngredient.CODEC, true).build();

    @SubscribeEvent
    public static void register(RegisterDataMapTypesEvent event) {
        event.register(SKULL_DROPS);
        event.register(SPAWN_EQUIPMENT);
        event.register(BREWING_INGREDIENTS);
    }

    public record BrewingIngredient(Optional<Holder<Potion>> potion, int seconds, int potency) {
        public static final Codec<BrewingIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Potion.CODEC.optionalFieldOf("potion").forGetter(BrewingIngredient::potion),
                Codec.intRange(0, 3600).optionalFieldOf("seconds", 0).forGetter(BrewingIngredient::seconds),
                Codec.intRange(0, 100).optionalFieldOf("potency", 0).forGetter(BrewingIngredient::potency)
        ).apply(instance, BrewingIngredient::new));
    }

    public record SkullDrop(Item skull, float chance) {
        public static final Codec<SkullDrop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("skull").forGetter(SkullDrop::skull),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance", 0.1F).forGetter(SkullDrop::chance)
        ).apply(instance, SkullDrop::new));
    }

    public record SpawnEquipment(float chance, Map<EquipmentSlot, Item> items) {
        public static final Codec<SpawnEquipment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("chance", 1.0F).forGetter(SpawnEquipment::chance),
                Codec.unboundedMap(EquipmentSlot.CODEC, BuiltInRegistries.ITEM.byNameCodec()).fieldOf("items").forGetter(SpawnEquipment::items)
        ).apply(instance, SpawnEquipment::new));
    }
}

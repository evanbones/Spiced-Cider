package com.evandev.spicedcider.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapDecorations;

import java.util.Map;
import java.util.WeakHashMap;

public final class MapIconProperty {
    private static final float PLAIN = 0.0F;
    private static final float UNKNOWN = 1.0F;
    private static final ResourceLocation CUSTOM_DECORATIONS = ResourceLocation.fromNamespaceAndPath("moonlight", "custom_map_decorations");
    private static final Map<ResourceLocation, Float> ICONS = Map.ofEntries(
            icon("minecraft:red_x", 2), icon("minecraft:monument", 3), icon("minecraft:mansion", 4), icon("minecraft:trial_chambers", 5),
            icon("minecraft:jungle_temple", 6), icon("minecraft:swamp_hut", 7), icon("minecraft:desert_pyramid", 8), icon("minecraft:ancient_city", 9),
            icon("supplementaries:ancient_city", 9), icon("minecraft:mineshaft", 10), icon("minecraft:ocean_ruin_warm", 11), icon("supplementaries:ocean_ruin", 11),
            icon("minecraft:abandoned_camp", 12), icon("minecraft:village_plains", 13), icon("minecraft:village_desert", 14), icon("minecraft:village_savanna", 15),
            icon("minecraft:village_snowy", 16), icon("minecraft:village_taiga", 17), icon("spicedcider:ruined_portal", 18));
    private static final Map<Object, Float> CUSTOM_CACHE = new WeakHashMap<>();

    public static float get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        boolean target = false;
        MapDecorations decorations = stack.get(DataComponents.MAP_DECORATIONS);
        if (decorations != null) {
            for (MapDecorations.Entry entry : decorations.decorations().values()) {
                target = true;
                Float icon = entry.type().unwrapKey().map(key -> ICONS.get(key.location())).orElse(null);
                if (icon != null) return icon;
            }
        }
        DataComponentType<?> customType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(CUSTOM_DECORATIONS);
        Object custom = customType == null ? null : stack.get(customType);
        if (custom != null) {
            synchronized (CUSTOM_CACHE) {
                return CUSTOM_CACHE.computeIfAbsent(custom, value -> customIcon(customType, value));
            }
        }
        return target || stack.getComponentsPatch().get(DataComponents.MAP_COLOR) != null ? UNKNOWN : PLAIN;
    }

    @SuppressWarnings("unchecked")
    private static float customIcon(DataComponentType<?> type, Object value) {
        Codec<Object> codec = (Codec<Object>) type.codec();
        ClientLevel level = Minecraft.getInstance().level;
        if (codec == null || level == null) return UNKNOWN;
        Tag tag = codec.encodeStart(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), value).result().orElse(null);
        if (!(tag instanceof CompoundTag markers)) return UNKNOWN;
        for (String key : markers.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(markers.getCompound(key).getString("type"));
            Float icon = id == null ? null : ICONS.get(id);
            if (icon != null) return icon;
        }
        return UNKNOWN;
    }

    private static Map.Entry<ResourceLocation, Float> icon(String id, int index) {
        return Map.entry(ResourceLocation.parse(id), (float) index);
    }
}

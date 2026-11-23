package com.evandev.spicedcider.compat.supplementaries;

import com.evandev.spicedcider.SpicedCider;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.supplementaries.common.items.SignPostItem;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CanvasWaySigns {
    private static final Map<ResourceLocation, WoodType> TYPES = new LinkedHashMap<>();
    private static final Map<WoodType, DeferredItem<Item>> ITEMS = new IdentityHashMap<>();

    public static List<String> prefixes() {
        List<String> prefixes = new ArrayList<>();
        prefixes.add("");
        for (DyeColor color : DyeColor.values()) prefixes.add(color.getName() + "_");
        return prefixes;
    }

    public static void register(DeferredRegister.Items items) {
        for (String prefix : prefixes()) {
            ResourceLocation id = typeId(prefix);
            DeferredItem<Item>[] holder = new DeferredItem[1];
            holder[0] = items.register(prefix + "canvas_way_sign", () -> {
                WoodType type = create(id, prefix);
                ITEMS.put(type, holder[0]);
                return new SignPostItem(ModRegistry.WAY_SIGN_WALL.get(), new Item.Properties().stacksTo(16), type);
            });
        }
    }

    private static WoodType create(ResourceLocation id, String prefix) {
        return TYPES.computeIfAbsent(id, key -> {
            var sign = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("farmersdelight", prefix + "canvas_sign"));
            return new WoodType(key, sign, sign);
        });
    }

    public static ResourceLocation typeId(String prefix) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, prefix + "canvas");
    }

    @Nullable
    public static WoodType type(ResourceLocation id) {
        return TYPES.get(id);
    }

    @Nullable
    public static Item item(WoodType type) {
        DeferredItem<Item> item = ITEMS.get(type);
        return item == null ? null : item.get();
    }

    public static Collection<WoodType> types() {
        return TYPES.values();
    }

    public static ResourceLocation modelId(WoodType type) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "block/way_signs/" + type.getId().getPath());
    }
}

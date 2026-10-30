package com.evandev.spicedcider.content.smithing;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class TrimStyling {

    public static final ResourceLocation DESIGN = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "trim");
    public static final TagKey<Item> TRIMMABLE_TOOLS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "trimmable_tools"));
    private static final String MARKER_PREFIX = "trim_pattern/";

    public static final Map<Item, ResourceLocation> MODIFIERS = new LinkedHashMap<>();

    static {
        MODIFIERS.put(Items.GLOW_INK_SAC, ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "emissive_trim"));
        MODIFIERS.put(Items.GLOWSTONE_DUST, ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "pulse_trim"));
        MODIFIERS.put(Items.PRISMARINE_SHARD, ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "faded_trim"));
    }

    public static boolean isTemplate(ItemStack stack) {
        return stack.is(ItemTags.TRIM_TEMPLATES);
    }

    public static boolean isTrimmable(ItemStack stack) {
        return stack.is(ItemTags.TRIMMABLE_ARMOR) || stack.is(TRIMMABLE_TOOLS);
    }

    public static Optional<Holder.Reference<TrimPattern>> pattern(HolderLookup.Provider registries, ItemStack template) {
        return TrimPatterns.getFromTemplate(registries, template);
    }

    public static Optional<Holder.Reference<TrimPattern>> pattern(HolderLookup.Provider registries, String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) return Optional.empty();
        return registries.lookupOrThrow(Registries.TRIM_PATTERN).get(ResourceKey.create(Registries.TRIM_PATTERN, location));
    }

    public static Optional<Holder.Reference<TrimMaterial>> material(HolderLookup.Provider registries, ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();
        return TrimMaterials.getFromIngredient(registries, stack);
    }

    public static Holder<TrimMaterial> previewMaterial(HolderLookup.Provider registries, ItemStack input, ItemStack addition) {
        Optional<Holder.Reference<TrimMaterial>> fromAddition = material(registries, addition);
        if (fromAddition.isPresent()) return fromAddition.get();
        ArmorTrim existing = input.get(DataComponents.TRIM);
        if (existing != null) return existing.material();
        return registries.lookupOrThrow(Registries.TRIM_MATERIAL).getOrThrow(TrimMaterials.IRON);
    }

    public static boolean isModifier(ItemStack stack) {
        return modifierComponent(stack) != null;
    }

    public static boolean isAddition(HolderLookup.Provider registries, ItemStack stack) {
        return isModifier(stack) || material(registries, stack).isPresent();
    }

    public static ResourceLocation marker(ResourceKey<TrimPattern> pattern) {
        ResourceLocation id = pattern.location();
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, MARKER_PREFIX + id.getNamespace() + "/" + id.getPath());
    }

    public static Optional<String> patternFromMarker(ResourceLocation marker) {
        if (!marker.getNamespace().equals(SpicedCider.MOD_ID) || !marker.getPath().startsWith(MARKER_PREFIX)) return Optional.empty();
        String rest = marker.getPath().substring(MARKER_PREFIX.length());
        int split = rest.indexOf('/');
        if (split <= 0) return Optional.empty();
        return Optional.of(rest.substring(0, split) + ":" + rest.substring(split + 1));
    }

    public static ItemStack withTrim(ItemStack input, Holder<TrimPattern> pattern, Holder<TrimMaterial> material) {
        ItemStack result = input.copyWithCount(1);
        result.set(DataComponents.TRIM, new ArmorTrim(material, pattern));
        return result;
    }

    public static ItemStack trimResult(HolderLookup.Provider registries, ItemStack input, ItemStack addition, String patternId) {
        if (!isTrimmable(input)) return ItemStack.EMPTY;
        Optional<Holder.Reference<TrimPattern>> pattern = pattern(registries, patternId);
        if (pattern.isEmpty()) return ItemStack.EMPTY;

        ArmorTrim existing = input.get(DataComponents.TRIM);
        Holder<TrimMaterial> material = material(registries, addition).map(holder -> (Holder<TrimMaterial>) holder)
                .orElse(existing == null ? null : existing.material());
        if (material == null) return ItemStack.EMPTY;
        if (existing != null && existing.hasPatternAndMaterial(pattern.get(), material)) return ItemStack.EMPTY;
        return withTrim(input, pattern.get(), material);
    }

    public static boolean trimConsumesAddition(HolderLookup.Provider registries, ItemStack addition) {
        return material(registries, addition).isPresent();
    }

    public static ItemStack modifierResult(ItemStack input, ItemStack addition) {
        if (!input.has(DataComponents.TRIM) || !input.is(ItemTags.TRIMMABLE_ARMOR)) return ItemStack.EMPTY;
        DataComponentType<Unit> type = modifierComponent(addition);
        if (type == null || input.has(type)) return ItemStack.EMPTY;
        ItemStack result = input.copyWithCount(1);
        result.set(type, Unit.INSTANCE);
        return result;
    }

    public static ItemStack withoutTrim(ItemStack stack) {
        ItemStack result = stack.copy();
        result.remove(DataComponents.TRIM);
        for (ResourceLocation id : MODIFIERS.values()) {
            DataComponentType<?> type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(id);
            if (type != null) result.remove(type);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static @Nullable DataComponentType<Unit> modifierComponent(ItemStack stack) {
        ResourceLocation id = MODIFIERS.get(stack.getItem());
        if (id == null) return null;
        return (DataComponentType<Unit>) BuiltInRegistries.DATA_COMPONENT_TYPE.get(id);
    }
}

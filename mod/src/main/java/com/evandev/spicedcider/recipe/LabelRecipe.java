package com.evandev.spicedcider.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class LabelRecipe extends CustomRecipe {
    public static final ResourceLocation ITEM_LABEL = ResourceLocation.fromNamespaceAndPath("etcetera", "item_label");
    public static final int MAX_LABELS = 4;

    public static final MapCodec<LabelRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(o -> o.bookInfo)
                    )
                    .apply(i, LabelRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, LabelRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC,
            o -> o.bookInfo,
            LabelRecipe::new
    );

    public static final RecipeSerializer<LabelRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public @NotNull MapCodec<LabelRecipe> codec() {
            return MAP_CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, LabelRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    private final CraftingBookCategory bookInfo;

    public LabelRecipe(final CraftingBookCategory bookInfo) {
        super(bookInfo);
        this.bookInfo = bookInfo;
    }

    public static boolean isLabel(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(ITEM_LABEL);
    }

    public static int labelCount(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int count = 0;
        while (count < MAX_LABELS && tag.contains("Label" + (count + 1))) count++;
        return count;
    }

    @Override
    public boolean matches(final CraftingInput input, final @NotNull Level level) {
        if (input.ingredientCount() != 2) return false;
        ItemStack label = ItemStack.EMPTY;
        ItemStack target = ItemStack.EMPTY;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) continue;
            if (isLabel(stack) && stack.has(DataComponents.CUSTOM_NAME) && label.isEmpty()) {
                label = stack;
            } else if (target.isEmpty()) {
                target = stack;
            } else {
                return false;
            }
        }
        return !label.isEmpty() && !target.isEmpty() && labelCount(target) < MAX_LABELS;
    }

    @Override
    public @NotNull ItemStack assemble(final CraftingInput input, HolderLookup.@NotNull Provider registries) {
        ItemStack label = ItemStack.EMPTY;
        ItemStack target = ItemStack.EMPTY;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) continue;
            if (isLabel(stack) && stack.has(DataComponents.CUSTOM_NAME) && label.isEmpty()) {
                label = stack;
            } else {
                target = stack;
            }
        }
        if (label.isEmpty() || target.isEmpty()) return ItemStack.EMPTY;
        int count = labelCount(target);
        if (count >= MAX_LABELS) return ItemStack.EMPTY;

        ItemStack result = target.copyWithCount(1);
        CompoundTag tag = result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putString("Label" + (count + 1), label.getHoverName().getString());
        result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<LabelRecipe> getSerializer() {
        return SERIALIZER;
    }
}

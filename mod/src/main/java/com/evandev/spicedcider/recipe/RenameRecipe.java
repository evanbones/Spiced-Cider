package com.evandev.spicedcider.recipe;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


public class RenameRecipe extends CustomRecipe {
    public static final MapCodec<RenameRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                            CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(o -> o.bookInfo)
                    )
                    .apply(i, RenameRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, RenameRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC,
            o -> o.bookInfo,
            RenameRecipe::new
    );

    public static final RecipeSerializer<RenameRecipe> SERIALIZER = new RecipeSerializer<>() {
        @Override
        public @NotNull MapCodec<RenameRecipe> codec() {
            return MAP_CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, RenameRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };

    private final CraftingBookCategory bookInfo;

    public RenameRecipe(
            final CraftingBookCategory bookInfo
    ) {
        super(bookInfo);
        this.bookInfo = bookInfo;
    }


    public boolean matches(final CraftingInput input, final @NotNull Level level) {
        if (input.ingredientCount() != 2) {
            return false;
        } else {
            boolean hasTarget = false;
            boolean hasNameTag = false;
            for (int slot = 0; slot < input.size(); slot++) {
                ItemStack itemStack = input.getItem(slot);
                if (!itemStack.isEmpty()) {
                    if (!(itemStack.getItem() instanceof NameTagItem)) {
                        if (hasTarget) {
                            return false;
                        }
                        hasTarget = true;
                    } else {
                        if (hasNameTag) {
                            return false;
                        }
                        hasNameTag = true;
                    }
                }
            }
            return hasNameTag && hasTarget;
        }
    }

    public @NotNull ItemStack assemble(final CraftingInput input, HolderLookup.@NotNull Provider registries) {
        Component customName = null;
        ItemStack targetStack = ItemStack.EMPTY;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack itemStack = input.getItem(slot);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof NameTagItem) {
                    customName = itemStack.get(DataComponents.CUSTOM_NAME);
                } else {
                    targetStack = itemStack;
                }
            }
        }

        if (!targetStack.isEmpty()) {
            ItemStack result = targetStack.copyWithCount(1);
            result.set(DataComponents.CUSTOM_NAME, customName);
            return result;
        } else {
            return ItemStack.EMPTY;
        }
    }


    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }


    @Override
    public @NotNull RecipeSerializer<RenameRecipe> getSerializer() {
        return SERIALIZER;
    }
}
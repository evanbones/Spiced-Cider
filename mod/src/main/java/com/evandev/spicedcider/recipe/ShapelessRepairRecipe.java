package com.evandev.spicedcider.recipe;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRepairRecipe extends ShapelessRecipe {
    public final Item baseItem;
    public final Ingredient addition;
    public final int additionCount;

    public ShapelessRepairRecipe(Item baseItem, Ingredient addition, int additionCount) {
        super("", CraftingBookCategory.EQUIPMENT, buildPreviewResult(baseItem), buildIngredients(baseItem, addition, additionCount));
        this.baseItem = baseItem;
        this.addition = addition;
        this.additionCount = additionCount;
    }

    private static ItemStack buildPreviewResult(Item item) {
        ItemStack stack = new ItemStack(item);
        stack.setDamageValue(1);
        return stack;
    }

    private static NonNullList<Ingredient> buildIngredients(Item item, Ingredient addition, int additionCount) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(item));
        for (int i = 0; i < additionCount; i++) {
            ingredients.add(addition);
        }
        return ingredients;
    }

    private ItemStack findBase(CraftingInput input) {
        ItemStack found = null;
        for (ItemStack stack : input.items()) {
            if (stack.is(baseItem) && stack.getDamageValue() > 0) {
                if (found != null) return null;
                found = stack;
            }
        }
        return found;
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        ItemStack base = findBase(input);
        if (base == null) return false;

        long units = input.items().stream().filter(addition).count();
        long empty = input.items().stream().filter(ItemStack::isEmpty).count();
        if (units <= 0 || units > additionCount) return false;
        if (empty != (input.size() - units - 1)) return false;

        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, HolderLookup.@NotNull Provider registries) {
        ItemStack base = findBase(input);
        if (base == null) return ItemStack.EMPTY;

        long units = input.items().stream().filter(addition).count();
        if (units <= 0 || units > additionCount) return ItemStack.EMPTY;

        ItemStack output = new ItemStack(baseItem);
        output.applyComponents(base.getComponentsPatch());
        output.setDamageValue(Math.max(0, base.getDamageValue() - (int) Math.ceil((base.getMaxDamage() * units) / (double) additionCount)));
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SHAPELESS_REPAIR.get();
    }

    public static List<RecipeHolder<ShapelessRepairRecipe>> generateAll() {
        List<RecipeHolder<ShapelessRepairRecipe>> result = new ArrayList<>();

        for (Item item : BuiltInRegistries.ITEM) {
            if (item.getDefaultInstance().getMaxDamage() <= 0) continue;

            Ingredient repairIngredient = getRepairIngredient(item);
            if (repairIngredient == null || repairIngredient.isEmpty()) continue;

            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                    SpicedCider.MOD_ID,
                    "repair/" + itemId.getNamespace() + "/" + itemId.getPath()
            );

            result.add(new RecipeHolder<>(recipeId, new ShapelessRepairRecipe(item, repairIngredient, 4)));
        }

        return result;
    }

    private static Ingredient getRepairIngredient(Item item) {
        if (item instanceof TieredItem tiered) {
            Ingredient ingredient = tiered.getTier().getRepairIngredient();
            return ingredient.isEmpty() ? null : ingredient;
        }
        if (item instanceof ArmorItem armor) {
            Ingredient ingredient = armor.getMaterial().value().repairIngredient().get();
            return ingredient.isEmpty() ? null : ingredient;
        }
        return null;
    }

    public static class Serializer implements RecipeSerializer<ShapelessRepairRecipe> {
        public static final MapCodec<ShapelessRepairRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("baseItem").forGetter(r -> r.baseItem),
                Ingredient.CODEC_NONEMPTY.fieldOf("addition").forGetter(r -> r.addition),
                Codec.INT.fieldOf("additionCount").forGetter(r -> r.additionCount)
        ).apply(instance, ShapelessRepairRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRepairRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    ByteBufCodecs.registry(Registries.ITEM).encode(buf, recipe.baseItem);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.addition);
                    buf.writeVarInt(recipe.additionCount);
                },
                buf -> {
                    Item item = ByteBufCodecs.registry(Registries.ITEM).decode(buf);
                    Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    int count = buf.readVarInt();
                    return new ShapelessRepairRecipe(item, ingredient, count);
                }
        );

        @Override
        public @NotNull MapCodec<ShapelessRepairRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ShapelessRepairRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

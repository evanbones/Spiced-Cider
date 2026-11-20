package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.casting.CrucibleMeltingRecipe;
import com.evandev.spicedcider.content.casting.MouldCastingRecipe;
import com.evandev.spicedcider.content.casting.smeltery.SmelteryAlloyingRecipe;
import com.evandev.spicedcider.recipe.AnvilSmithingRecipe;
import com.evandev.spicedcider.recipe.LabelRecipe;
import com.evandev.spicedcider.recipe.PocketJukeboxDyeRecipe;
import com.evandev.spicedcider.recipe.RenameRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SpicedCider.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RenameRecipe>> RENAME_ITEM = RECIPE_SERIALIZERS.register("rename_item", () -> RenameRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LabelRecipe>> LABEL_ITEM = RECIPE_SERIALIZERS.register("label_item", () -> LabelRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PocketJukeboxDyeRecipe>> POCKET_JUKEBOX_DYE = RECIPE_SERIALIZERS.register("pocket_jukebox_dye", () -> PocketJukeboxDyeRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnvilSmithingRecipe>> ANVIL_SMITHING = RECIPE_SERIALIZERS.register("anvil_smithing", () -> AnvilSmithingRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrucibleMeltingRecipe>> CRUCIBLE_MELTING = RECIPE_SERIALIZERS.register("crucible_melting", () -> CrucibleMeltingRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MouldCastingRecipe>> MOULD_CASTING = RECIPE_SERIALIZERS.register("mould_casting", () -> MouldCastingRecipe.SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrucibleMeltingRecipe>> SMELTERY_MELTING = RECIPE_SERIALIZERS.register("smeltery_melting", () -> CrucibleMeltingRecipe.SMELTERY_SERIALIZER);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SmelteryAlloyingRecipe>> SMELTERY_ALLOYING = RECIPE_SERIALIZERS.register("smeltery_alloying", () -> SmelteryAlloyingRecipe.SERIALIZER);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AnvilSmithingRecipe>> ANVIL_SMITHING_TYPE = RECIPE_TYPES.register("anvil_smithing", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "anvil_smithing")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<CrucibleMeltingRecipe>> CRUCIBLE_MELTING_TYPE = RECIPE_TYPES.register("crucible_melting", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "crucible_melting")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<MouldCastingRecipe>> MOULD_CASTING_TYPE = RECIPE_TYPES.register("mould_casting", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "mould_casting")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<CrucibleMeltingRecipe>> SMELTERY_MELTING_TYPE = RECIPE_TYPES.register("smeltery_melting", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "smeltery_melting")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SmelteryAlloyingRecipe>> SMELTERY_ALLOYING_TYPE = RECIPE_TYPES.register("smeltery_alloying", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "smeltery_alloying")));
}

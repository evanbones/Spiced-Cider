package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.quark.QuarkRunes;
import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.evandev.spicedcider.content.smithing.AnvilSmithing;
import com.evandev.spicedcider.recipe.AnvilSmithingRecipe;
import com.evandev.spicedcider.registry.ModRecipeSerializers;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class AnvilSmithingEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<ItemStack> bases;
    private final EmiIngredient addition;
    private final ItemStack additionStack;
    private final BiFunction<ItemStack, ItemStack, ItemStack> smith;
    private final List<EmiStack> outputs = new ArrayList<>();
    private final int uniq = new Random().nextInt();

    private AnvilSmithingEmiRecipe(ResourceLocation id, List<ItemStack> bases, EmiIngredient addition, ItemStack additionStack, BiFunction<ItemStack, ItemStack, ItemStack> smith) {
        this.id = id;
        this.addition = addition;
        this.additionStack = additionStack;
        this.smith = smith;
        List<ItemStack> usable = new ArrayList<>();
        for (ItemStack base : bases) {
            if (ReliableRemoverCompat.isHiddenFromViewers(base.getItem())) continue;
            ItemStack result = smith.apply(base, additionStack);
            if (!result.isEmpty()) {
                usable.add(base);
                this.outputs.add(EmiStack.of(result));
            }
        }
        this.bases = usable;
    }

    public static void register(EmiRegistry registry) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        for (RecipeHolder<AnvilSmithingRecipe> holder : registry.getRecipeManager().getAllRecipesFor(ModRecipeSerializers.ANVIL_SMITHING_TYPE.get())) {
            AnvilSmithingRecipe recipe = holder.value();
            ItemStack[] additions = Arrays.stream(recipe.addition().getItems()).filter(stack -> !ReliableRemoverCompat.isHiddenFromViewers(stack.getItem())).toArray(ItemStack[]::new);
            if (additions.length == 0) continue;
            add(registry, new AnvilSmithingEmiRecipe(holder.id(), List.of(recipe.base().getItems()), EmiIngredient.of(Arrays.stream(additions).map(EmiStack::of).toList()), additions[0],
                    (base, addition) -> recipe.assemble(new SmithingRecipeInput(ItemStack.EMPTY, base, addition), level.registryAccess())));
        }

        for (RecipeHolder<SmithingRecipe> holder : AnvilSmithing.tabletRecipes(level)) {
            SmithingRecipe recipe = holder.value();
            ItemStack[] tablets = AnvilSmithing.tabletIngredient(recipe, "template").getItems();
            List<ItemStack> bases = List.of(AnvilSmithing.tabletIngredient(recipe, "base").getItems());
            if (tablets.length == 0 || bases.isEmpty()) continue;
            ResourceLocation id = synthetic("tablet", holder.id());
            add(registry, new AnvilSmithingEmiRecipe(id, bases, EmiStack.of(tablets[0]), tablets[0],
                    (base, tablet) -> recipe.assemble(new SmithingRecipeInput(tablet, base, ItemStack.EMPTY), level.registryAccess())));
        }

        if (QuarkRunes.isLoaded()) {
            List<ItemStack> bases = new ArrayList<>();
            for (ItemStack stack : List.of(new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.DIAMOND_PICKAXE), new ItemStack(Items.DIAMOND_CHESTPLATE), new ItemStack(Items.BOW), new ItemStack(Items.CROSSBOW))) {
                stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                bases.add(stack);
            }
            for (String color : QuarkRunes.COLORS) {
                ItemStack rune = QuarkRunes.rune(color);
                add(registry, new AnvilSmithingEmiRecipe(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/anvil/rune/" + color), bases, EmiStack.of(rune), rune,
                        (base, r) -> QuarkRunes.display(base, QuarkRunes.color(r))));
            }
        }
    }

    private static void add(EmiRegistry registry, AnvilSmithingEmiRecipe recipe) {
        if (!recipe.bases.isEmpty()) registry.addRecipe(recipe);
    }

    private static ResourceLocation synthetic(String kind, ResourceLocation id) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/anvil/" + kind + "/" + id.getNamespace() + "/" + id.getPath());
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.ANVIL_REPAIRING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiIngredient.of(this.bases.stream().map(EmiStack::of).toList()), this.addition);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
        widgets.addGeneratedSlot(random -> EmiStack.of(this.base(random)), this.uniq, 0, 0);
        widgets.addSlot(this.addition, 49, 0);
        widgets.addGeneratedSlot(random -> EmiStack.of(this.smith.apply(this.base(random), this.additionStack)), this.uniq, 107, 0).recipeContext(this);
    }

    private ItemStack base(Random random) {
        return this.bases.get(random.nextInt(this.bases.size()));
    }
}

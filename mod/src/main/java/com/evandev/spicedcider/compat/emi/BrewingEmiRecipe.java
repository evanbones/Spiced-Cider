package com.evandev.spicedcider.compat.emi;

import cc.cassian.cauldrons.registry.CauldronModItems;
import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.brewing.CauldronBrewing;
import com.evandev.spicedcider.registry.ModDataMaps;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.component.DataComponents;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BrewingEmiRecipe extends BasicEmiRecipe {
    private static final int WIDTH = 134;
    private static final int SLOT = 18;
    private static final int PER_ROW = 3;
    private static final TagKey<Item> SPLASH = TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("toil_and_trouble", "creates_splash_potions"));
    private static final TagKey<Item> LINGERING = TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("toil_and_trouble", "creates_lingering_potions"));

    private final EmiStack base;
    private final List<Entry> entries;
    private final EmiStack output;
    private final Component note;

    private record Entry(EmiIngredient stack, Component tooltip) {
    }

    private BrewingEmiRecipe(EmiRecipeCategory category, ResourceLocation id, EmiStack base, List<Entry> entries, List<EmiStack> outputs, Component note) {
        super(category, id, WIDTH, rows(entries.size()) * SLOT + (note == null ? 0 : 12));
        this.base = base;
        this.entries = entries;
        this.output = outputs.getFirst();
        this.note = note;
        List<EmiIngredient> inputs = new ArrayList<>();
        inputs.add(base);
        entries.forEach(entry -> inputs.add(entry.stack()));
        this.inputs = inputs;
        this.outputs = outputs;
    }

    private static int rows(int count) {
        return Math.max(1, (count + PER_ROW - 1) / PER_ROW);
    }

    public static void register(EmiRegistry registry) {
        registry.setDefaultComparison(CauldronModItems.CAULDRON_CONTENTS, Comparison.compareData(stack -> stack.getItemStack().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion()));
        EmiStack cauldron = EmiStack.of(Items.CAULDRON);
        EmiRecipeCategory category = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "cauldron_brewing"),
                EmiStack.of(PotionContents.createItemStack(Items.POTION, Potions.SWIFTNESS)));
        registry.addCategory(category);
        registry.addWorkstation(category, cauldron);

        EmiStack clear = EmiStack.of(PotionContents.createItemStack(Items.POTION, Potions.AWKWARD));
        Map<ResourceKey<Potion>, List<Map.Entry<Item, ModDataMaps.BrewingIngredient>>> byPotion = new LinkedHashMap<>();
        List<Map.Entry<Item, ModDataMaps.BrewingIngredient>> boosters = new ArrayList<>();
        for (Map.Entry<ResourceKey<Item>, ModDataMaps.BrewingIngredient> entry : BuiltInRegistries.ITEM.getDataMap(ModDataMaps.BREWING_INGREDIENTS).entrySet()) {
            Item item = BuiltInRegistries.ITEM.get(entry.getKey());
            ModDataMaps.BrewingIngredient ingredient = entry.getValue();
            Map.Entry<Item, ModDataMaps.BrewingIngredient> pair = Map.entry(item, ingredient);
            Optional<ResourceKey<Potion>> key = ingredient.potion().flatMap(Holder::unwrapKey);
            if (key.isPresent()) {
                byPotion.computeIfAbsent(key.get(), k -> new ArrayList<>()).add(pair);
            } else {
                boosters.add(pair);
            }
        }

        byPotion.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().location().getPath()))
                .forEach(entry -> {
                    Holder<Potion> potion = BuiltInRegistries.POTION.getHolderOrThrow(entry.getKey());
                    List<Map.Entry<Item, ModDataMaps.BrewingIngredient>> items = entry.getValue();
                    items.sort(Comparator.comparingInt(pair -> pair.getValue().potency()));
                    List<Entry> entries = items.stream().map(pair -> new Entry(EmiStack.of(pair.getKey()), describe(pair.getValue()))).toList();
                    List<EmiStack> outputs = List.of(EmiStack.of(PotionContents.createItemStack(Items.POTION, potion)),
                            EmiStack.of(PotionContents.createItemStack(Items.SPLASH_POTION, potion)),
                            EmiStack.of(PotionContents.createItemStack(Items.LINGERING_POTION, potion)),
                            EmiStack.of(PotionContents.createItemStack(Items.TIPPED_ARROW, potion)),
                            EmiStack.of(PotionContents.createItemStack(CauldronModItems.CAULDRON_CONTENTS, potion)));
                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID,
                            "/brewing/" + entry.getKey().location().getNamespace() + "/" + entry.getKey().location().getPath());
                    registry.addRecipe(new BrewingEmiRecipe(category, id, clear, entries, outputs, null));
                });

        if (!boosters.isEmpty()) {
            List<Entry> entries = boosters.stream().map(pair -> new Entry(EmiStack.of(pair.getKey()), describe(pair.getValue()))).toList();
            EmiStack output = EmiStack.of(brewed(Items.POTION, Potions.SWIFTNESS, 180, 14));
            registry.addRecipe(new BrewingEmiRecipe(category, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/brewing/boosters"),
                    EmiStack.of(brewed(Items.POTION, Potions.SWIFTNESS, 0, 0)), entries, List.of(output), Component.translatable("spicedcider.emi.brewing.boosters")));
        }

        EmiStack sample = EmiStack.of(brewed(Items.POTION, Potions.SWIFTNESS, 0, 0));
        registry.addRecipe(new BrewingEmiRecipe(category, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/brewing/splash"), sample,
                List.of(new Entry(EmiIngredient.of(SPLASH), Component.translatable("spicedcider.emi.brewing.splash"))),
                List.of(EmiStack.of(brewed(Items.SPLASH_POTION, Potions.SWIFTNESS, 0, 0))), null));
        registry.addRecipe(new BrewingEmiRecipe(category, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/brewing/lingering"), sample,
                List.of(new Entry(EmiIngredient.of(LINGERING), Component.translatable("spicedcider.emi.brewing.lingering"))),
                List.of(EmiStack.of(brewed(Items.LINGERING_POTION, Potions.SWIFTNESS, 0, 0))), null));
    }

    private static ItemStack brewed(Item item, Holder<Potion> potion, int seconds, int potency) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(potion), Optional.empty(), CauldronBrewing.effects(potion, seconds, potency)));
        return stack;
    }

    private static Component describe(ModDataMaps.BrewingIngredient ingredient) {
        if (ingredient.seconds() > 0 && ingredient.potency() > 0) {
            return Component.translatable("spicedcider.emi.brewing.ingredient", time(ingredient.seconds()), ingredient.potency());
        }
        if (ingredient.seconds() > 0) {
            return Component.translatable("spicedcider.emi.brewing.time", time(ingredient.seconds()));
        }
        return Component.translatable("spicedcider.emi.brewing.potency", ingredient.potency());
    }

    private static String time(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int height = rows(this.entries.size()) * SLOT;
        int middle = (height - SLOT) / 2;
        widgets.addSlot(this.base, 0, middle);
        widgets.addTexture(EmiTexture.PLUS, 21, middle + 4);
        for (int i = 0; i < this.entries.size(); i++) {
            Entry entry = this.entries.get(i);
            SlotWidget slot = widgets.addSlot(entry.stack(), 34 + (i % PER_ROW) * SLOT, (i / PER_ROW) * SLOT);
            slot.appendTooltip(entry.tooltip());
        }
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 34 + PER_ROW * SLOT + 2, middle + 1);
        widgets.addSlot(this.output, WIDTH - SLOT, middle).recipeContext(this);
        if (this.note != null) {
            widgets.addText(this.note, 0, height + 3, 0xFF404040, false);
        }
    }
}

package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.evandev.spicedcider.content.smithing.TrimStyling;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class TrimStylingEmiRecipe implements EmiRecipe {
    private static final ResourceLocation STYLING_TABLE = ResourceLocation.fromNamespaceAndPath("vanity", "styling_table");

    private final EmiRecipeCategory category;
    private final ResourceLocation id;
    private final EmiIngredient catalyst;
    private final List<ItemStack> armor;
    private final List<ItemStack> additions;
    private final BiFunction<ItemStack, ItemStack, ItemStack> style;
    private final List<EmiStack> outputs = new ArrayList<>();
    private final int uniq = new Random().nextInt();

    private TrimStylingEmiRecipe(EmiRecipeCategory category, ResourceLocation id, EmiIngredient catalyst, List<ItemStack> armor, List<ItemStack> additions, BiFunction<ItemStack, ItemStack, ItemStack> style) {
        this.category = category;
        this.id = id;
        this.catalyst = catalyst;
        this.armor = armor;
        this.additions = additions;
        this.style = style;
        for (ItemStack piece : armor) {
            ItemStack result = style.apply(piece, additions.getFirst());
            if (!result.isEmpty()) this.outputs.add(EmiStack.of(result));
        }
    }

    public static void register(EmiRegistry registry) {
        if (Minecraft.getInstance().level == null) return;
        RegistryAccess registries = Minecraft.getInstance().level.registryAccess();
        Item table = BuiltInRegistries.ITEM.get(STYLING_TABLE);
        EmiStack workstation = EmiStack.of(table);
        EmiRecipeCategory category = new EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "trim_styling"), workstation);
        registry.addCategory(category);
        registry.addWorkstation(category, workstation);

        List<ItemStack> armor = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.TRIMMABLE_ARMOR).forEach(holder -> {
            if (!ReliableRemoverCompat.isHiddenFromViewers(holder.value())) armor.add(new ItemStack(holder));
        });
        List<ItemStack> gear = new ArrayList<>(armor);
        BuiltInRegistries.ITEM.getTagOrEmpty(TrimStyling.TRIMMABLE_TOOLS).forEach(holder -> {
            if (!ReliableRemoverCompat.isHiddenFromViewers(holder.value()) && gear.stream().noneMatch(stack -> stack.is(holder.value()))) gear.add(new ItemStack(holder));
        });
        if (armor.isEmpty()) return;

        List<ItemStack> materials = new ArrayList<>();
        registries.lookupOrThrow(Registries.TRIM_MATERIAL).listElements()
                .map(material -> material.value().ingredient().value())
                .distinct()
                .filter(item -> !ReliableRemoverCompat.isHiddenFromViewers(item))
                .forEach(item -> materials.add(new ItemStack(item)));
        if (materials.isEmpty()) return;

        BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.TRIM_TEMPLATES).forEach(holder -> {
            if (ReliableRemoverCompat.isHiddenFromViewers(holder.value())) return;
            ItemStack template = new ItemStack(holder);
            TrimStyling.pattern(registries, template).ifPresent(pattern -> {
                ResourceLocation key = pattern.key().location();
                registry.addRecipe(new TrimStylingEmiRecipe(category,
                        ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/trim_styling/" + key.getNamespace() + "/" + key.getPath()),
                        EmiStack.of(template), gear, materials,
                        (piece, material) -> TrimStyling.material(registries, material)
                                .map(found -> TrimStyling.withTrim(piece, pattern, found))
                                .orElse(ItemStack.EMPTY)));
            });
        });

        Holder<TrimPattern> samplePattern = registries.lookupOrThrow(Registries.TRIM_PATTERN).listElements().findFirst().orElse(null);
        if (samplePattern == null) return;
        Holder<TrimMaterial> sampleMaterial = TrimStyling.previewMaterial(registries, ItemStack.EMPTY, ItemStack.EMPTY);
        List<ItemStack> trimmed = armor.stream().map(piece -> {
            ItemStack stack = piece.copy();
            stack.set(DataComponents.TRIM, new ArmorTrim(sampleMaterial, samplePattern));
            return stack;
        }).toList();
        TrimStyling.MODIFIERS.forEach((item, component) -> {
            if (!BuiltInRegistries.DATA_COMPONENT_TYPE.containsKey(component)) return;
            registry.addRecipe(new TrimStylingEmiRecipe(category,
                    ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "/trim_styling/modifier/" + component.getPath()),
                    EmiStack.EMPTY, trimmed, List.of(new ItemStack(item)), TrimStyling::modifierResult));
        });
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return this.category;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiIngredient.of(this.armor.stream().map(EmiStack::of).toList()), EmiIngredient.of(this.additions.stream().map(EmiStack::of).toList()));
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return this.catalyst.isEmpty() ? List.of() : List.of(this.catalyst);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 112;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        if (!this.catalyst.isEmpty()) {
            widgets.addSlot(this.catalyst, 0, 0).catalyst(true);
        }
        widgets.addGeneratedSlot(random -> EmiStack.of(this.armor(random)), this.uniq, 18, 0);
        widgets.addGeneratedSlot(random -> EmiStack.of(this.addition(random)), this.uniq, 36, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 1);
        widgets.addGeneratedSlot(random -> EmiStack.of(this.style.apply(this.armor(random), this.addition(random))), this.uniq, 94, 0).recipeContext(this);
    }

    private ItemStack armor(Random random) {
        return this.armor.get(random.nextInt(this.armor.size()));
    }

    private ItemStack addition(Random random) {
        random.nextInt();
        return this.additions.get(random.nextInt(this.additions.size()));
    }
}

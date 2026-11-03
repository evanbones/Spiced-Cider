package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.evandev.spicedcider.networking.LootDisplayPayload.Drop;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SpawnEggItem;
import com.evandev.spicedcider.registry.ModTags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class LootEmiRecipe extends BasicEmiRecipe {
    protected static final int SLOT = 18;
    public static final int MAX_ROWS = 4;

    private final int outputX;
    private final int outputY;
    private final int columns;
    private final List<List<Component>> tooltips = new ArrayList<>();

    protected LootEmiRecipe(EmiRecipeCategory category, ResourceLocation id, List<EmiIngredient> inputs, List<Drop> drops,
                            int outputX, int outputY, int columns, int minHeight) {
        this(category, id, inputs, drops, outputX, outputY, columns, minHeight, false);
    }

    protected LootEmiRecipe(EmiRecipeCategory category, ResourceLocation id, List<EmiIngredient> inputs, List<Drop> drops,
                            int outputX, int outputY, int columns, int minHeight, boolean spawnEggsReleaseMobs) {
        super(category, id, outputX + columns * SLOT, Math.max(minHeight, outputY + rows(drops.size(), columns) * SLOT));
        this.outputX = outputX;
        this.outputY = outputY;
        this.columns = columns;
        this.inputs = inputs;
        this.outputs = new ArrayList<>();
        for (Drop drop : drops) {
            int amount = drop.min() == drop.max() ? drop.min() : 1;
            this.outputs.add(EmiStack.of(drop.stack(), amount));
            this.tooltips.add(tooltip(drop, spawnEggsReleaseMobs));
        }
    }

    private static int rows(int count, int columns) {
        return Math.max(1, (count + columns - 1) / columns);
    }

    protected void addOutputs(WidgetHolder widgets) {
        for (int i = 0; i < outputs.size(); i++) {
            SlotWidget slot = widgets.addSlot(outputs.get(i), outputX + (i % columns) * SLOT, outputY + (i / columns) * SLOT).recipeContext(this);
            for (Component line : tooltips.get(i)) slot.appendTooltip(line);
        }
    }

    protected int outputRowsHeight() {
        return rows(outputs.size(), columns) * SLOT;
    }

    public static List<List<Drop>> pages(List<Drop> drops, int columns) {
        int size = columns * MAX_ROWS;
        List<List<Drop>> pages = new ArrayList<>();
        for (int i = 0; i < drops.size(); i += size) pages.add(drops.subList(i, Math.min(drops.size(), i + size)));
        return pages;
    }

    public static ResourceLocation pageId(ResourceLocation id, int page) {
        return page == 0 ? id : id.withSuffix("/" + (page + 1));
    }

    public static Component pageTitle(Component title, int page, int pages) {
        return pages <= 1 ? title : Component.translatable("emi.spicedcider.loot.page", title, page + 1, pages);
    }

    public static List<Drop> visible(List<Drop> drops) {
        return drops.stream().filter(drop -> !ReliableRemoverCompat.isHiddenFromViewers(drop.stack().getItem())).toList();
    }

    public static List<Drop> withoutDeniedMobs(List<Drop> drops) {
        return drops.stream().filter(drop -> !(drop.stack().getItem() instanceof SpawnEggItem egg)
                || !egg.getType(drop.stack()).is(ModTags.EntityTypes.DENY_SPAWN)).toList();
    }

    private static List<Component> tooltip(Drop drop, boolean spawnEggsReleaseMobs) {
        List<Component> lines = new ArrayList<>();
        if (spawnEggsReleaseMobs && drop.stack().getItem() instanceof SpawnEggItem egg) {
            lines.add(Component.translatable("emi.spicedcider.loot.releases_mob", egg.getType(drop.stack()).getDescription()));
        }
        if (drop.min() != drop.max()) {
            lines.add(Component.translatable("emi.spicedcider.loot.amount", drop.min(), drop.max()));
        }
        if (!drop.biomes().isEmpty()) {
            lines.add(Component.translatable("emi.spicedcider.loot.biomes", names(drop.biomes(), "biome")));
        }
        if (!drop.structures().isEmpty()) {
            lines.add(Component.translatable("emi.spicedcider.loot.structures", names(drop.structures(), "structure")));
        }
        return lines;
    }

    private static String names(List<String> ids, String kind) {
        return ids.stream().map(id -> name(id, kind)).distinct().collect(Collectors.joining(", "));
    }

    private static String name(String raw, String kind) {
        boolean tag = raw.startsWith("#");
        ResourceLocation id = ResourceLocation.tryParse(tag ? raw.substring(1) : raw);
        if (id == null) return raw;
        if (!tag && kind.equals("structure")) return StructureNames.name(id).getString();
        String key = (tag ? "tag.worldgen." + kind + "." : kind + ".") + id.getNamespace() + "." + id.getPath().replace('/', '.');
        if (I18n.exists(key)) return I18n.get(key);
        return StructureNames.prettify(id, tag);
    }
}

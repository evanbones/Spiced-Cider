package com.evandev.spicedcider.compat.emi;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.content.loot.LootDisplays;
import com.evandev.spicedcider.networking.LootDisplayPayload.Drop;
import com.evandev.spicedcider.networking.LootDisplayPayload.Table;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LootEmiCategories {

    private record Group(Component title, ResourceLocation first, List<ResourceLocation> inputs, List<Drop> drops) {
    }

    public static void register(EmiRegistry registry) {
        List<Table> tables = LootDisplays.tables();
        if (tables.isEmpty()) return;
        if (enabled(SpicedCiderConfig.CLIENT.emiSluicingCategory)) registerSluicing(registry, tables);
        if (enabled(SpicedCiderConfig.CLIENT.emiBarteringCategory)) registerBartering(registry, tables);
        if (enabled(SpicedCiderConfig.CLIENT.emiSniffingCategory)) registerSniffer(registry, tables);
        if (enabled(SpicedCiderConfig.CLIENT.emiArchaeologyCategory)) registerArchaeology(registry, tables);
        if (enabled(SpicedCiderConfig.CLIENT.emiStructureLootCategory)) registerStructures(registry, tables);
    }

    private static boolean enabled(ModConfigSpec.BooleanValue value) {
        return SpicedCiderConfig.clientOr(value, false);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, path);
    }

    private static EmiRecipeCategory category(EmiRegistry registry, String name, EmiStack icon, EmiStack... workstations) {
        EmiRecipeCategory category = new EmiRecipeCategory(id(name), icon);
        registry.addCategory(category);
        for (EmiStack workstation : workstations) registry.addWorkstation(category, workstation);
        return category;
    }

    private static void registerSluicing(EmiRegistry registry, List<Table> tables) {
        Item grate = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("spelunkery", "sluice_grate"));
        Block sluice = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("spelunkery", "wooden_sluice"));
        if (grate == Items.AIR || sluice == Blocks.AIR) return;
        EmiRecipeCategory category = null;
        for (Table table : of(tables, LootDisplays.SLUICING)) {
            String[] parts = table.id().getPath().substring(LootDisplays.SLUICE_PREFIX.length()).split("/");
            if (parts.length != 2) continue;
            List<EmiStack> fluids = fluids(parts[0]);
            if (fluids.isEmpty()) continue;
            EmiIngredient item = null;
            if (!parts[1].equals("passive")) {
                List<EmiStack> items = itemsByPath(parts[1]);
                if (items.isEmpty()) continue;
                item = EmiIngredient.of(items);
            }
            List<Drop> drops = LootEmiRecipe.withoutDeniedMobs(LootEmiRecipe.visible(table.drops()));
            if (drops.isEmpty()) continue;
            if (category == null) category = category(registry, "sluicing", EmiStack.of(grate), EmiStack.of(grate));
            List<List<Drop>> pages = LootEmiRecipe.pages(drops, SluicingEmiRecipe.COLUMNS);
            for (int page = 0; page < pages.size(); page++) {
                registry.addRecipe(new SluicingEmiRecipe(category, LootEmiRecipe.pageId(id("/sluicing/" + parts[0] + "/" + parts[1]), page), sluice, fluids, item, pages.get(page)));
            }
        }
    }

    private static void registerBartering(EmiRegistry registry, List<Table> tables) {
        for (Table table : of(tables, LootDisplays.BARTERING_KIND)) {
            List<Drop> drops = LootEmiRecipe.visible(table.drops());
            if (drops.isEmpty()) continue;
            EmiRecipeCategory category = category(registry, "bartering", EmiStack.of(Items.PIGLIN_HEAD));
            List<List<Drop>> pages = LootEmiRecipe.pages(drops, BarteringEmiRecipe.COLUMNS);
            for (int page = 0; page < pages.size(); page++) {
                registry.addRecipe(new BarteringEmiRecipe(category, LootEmiRecipe.pageId(id("/bartering"), page), pages.get(page)));
            }
        }
    }

    private static void registerSniffer(EmiRegistry registry, List<Table> tables) {
        for (Table table : of(tables, LootDisplays.SNIFFER)) {
            List<Drop> drops = LootEmiRecipe.visible(table.drops());
            List<EmiStack> diggable = new ArrayList<>();
            for (Holder<Block> block : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.SNIFFER_DIGGABLE_BLOCK)) {
                Item item = block.value().asItem();
                if (item != Items.AIR && !ReliableRemoverCompat.isHiddenFromViewers(item)) diggable.add(EmiStack.of(item));
            }
            if (drops.isEmpty() || diggable.isEmpty()) continue;
            EmiRecipeCategory category = category(registry, "sniffing", EmiStack.of(Items.SNIFFER_EGG), EmiStack.of(Items.SNIFFER_EGG));
            List<List<Drop>> pages = LootEmiRecipe.pages(drops, SnifferEmiRecipe.COLUMNS);
            for (int page = 0; page < pages.size(); page++) {
                registry.addRecipe(new SnifferEmiRecipe(category, LootEmiRecipe.pageId(id("/sniffing"), page), EmiIngredient.of(diggable), pages.get(page)));
            }
        }
    }

    private static void registerArchaeology(EmiRegistry registry, List<Table> tables) {
        Map<String, Group> groups = group(of(tables, LootDisplays.ARCHAEOLOGY));
        if (groups.isEmpty()) return;
        EmiRecipeCategory category = category(registry, "archaeology", EmiStack.of(Items.BRUSH), EmiStack.of(Items.BRUSH));
        for (Group group : sorted(groups)) {
            List<EmiStack> blocks = stacks(group.inputs());
            if (blocks.isEmpty()) blocks = List.of(EmiStack.of(Items.SUSPICIOUS_SAND), EmiStack.of(Items.SUSPICIOUS_GRAVEL));
            List<List<Drop>> pages = LootEmiRecipe.pages(group.drops(), ArchaeologyEmiRecipe.COLUMNS);
            for (int page = 0; page < pages.size(); page++) {
                registry.addRecipe(new ArchaeologyEmiRecipe(category,
                        LootEmiRecipe.pageId(id("/archaeology/" + group.first().getNamespace() + "/" + group.first().getPath()), page),
                        LootEmiRecipe.pageTitle(group.title(), page, pages.size()), blocks, pages.get(page)));
            }
        }
    }

    private static void registerStructures(EmiRegistry registry, List<Table> tables) {
        Map<String, Group> groups = group(of(tables, LootDisplays.STRUCTURE_LOOT));
        if (groups.isEmpty()) return;
        EmiRecipeCategory category = category(registry, "structure_loot", EmiStack.of(Items.CHEST));
        for (Group group : sorted(groups)) {
            List<ItemStack> containers = stacks(group.inputs()).stream().map(EmiStack::getItemStack).toList();
            if (containers.isEmpty()) containers = List.of(new ItemStack(Items.CHEST));
            List<List<Drop>> pages = LootEmiRecipe.pages(group.drops(), StructureLootEmiRecipe.COLUMNS);
            for (int page = 0; page < pages.size(); page++) {
                registry.addRecipe(new StructureLootEmiRecipe(category,
                        LootEmiRecipe.pageId(id("/structure_loot/" + group.first().getNamespace() + "/" + group.first().getPath()), page),
                        LootEmiRecipe.pageTitle(group.title(), page, pages.size()), containers, pages.get(page)));
            }
        }
    }

    private static List<Table> of(List<Table> tables, String kind) {
        return tables.stream().filter(table -> table.kind().equals(kind)).toList();
    }

    private static Map<String, Group> group(List<Table> tables) {
        Map<String, Group> groups = new LinkedHashMap<>();
        for (Table table : tables) {
            List<Drop> drops = LootEmiRecipe.visible(table.drops());
            if (drops.isEmpty()) continue;
            Component title = StructureNames.name(table.id());
            Group group = groups.computeIfAbsent(title.getString(), key -> new Group(title, table.id(), new ArrayList<>(), new ArrayList<>()));
            for (ResourceLocation input : table.inputs()) if (!group.inputs().contains(input)) group.inputs().add(input);
            for (Drop drop : drops) {
                boolean seen = group.drops().stream().anyMatch(existing -> sameStack(existing.stack(), drop.stack()));
                if (!seen) group.drops().add(drop);
            }
        }
        return groups;
    }

    private static boolean sameStack(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem() && Objects.equals(a.get(DataComponents.POTION_CONTENTS), b.get(DataComponents.POTION_CONTENTS));
    }

    private static List<Group> sorted(Map<String, Group> groups) {
        return groups.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.naturalOrder())).map(Map.Entry::getValue).toList();
    }

    private static List<EmiStack> stacks(List<ResourceLocation> ids) {
        List<EmiStack> stacks = new ArrayList<>();
        for (ResourceLocation id : ids) {
            Item item = BuiltInRegistries.ITEM.get(id);
            if (item == Items.AIR) item = BuiltInRegistries.BLOCK.get(id).asItem();
            if (item != Items.AIR && !ReliableRemoverCompat.isHiddenFromViewers(item)) stacks.add(EmiStack.of(item));
        }
        return stacks.stream().distinct().toList();
    }

    private static List<EmiStack> fluids(String name) {
        List<EmiStack> fluids = new ArrayList<>();
        for (Fluid fluid : BuiltInRegistries.FLUID) {
            if (fluid.isSource(fluid.defaultFluidState()) && BuiltInRegistries.FLUID.getKey(fluid).getPath().equals(name)) {
                fluids.add(EmiStack.of(fluid));
            }
        }
        return fluids;
    }

    private static List<EmiStack> itemsByPath(String name) {
        List<EmiStack> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item != Items.AIR && BuiltInRegistries.ITEM.getKey(item).getPath().equals(name) && !ReliableRemoverCompat.isHiddenFromViewers(item)) {
                items.add(EmiStack.of(item));
            }
        }
        return items;
    }
}

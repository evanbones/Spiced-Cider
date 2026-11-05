package com.evandev.spicedcider.content.loot;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.networking.LootDisplayPayload;
import com.evandev.spicedcider.networking.LootDisplayPayload.Drop;
import com.evandev.spicedcider.networking.LootDisplayPayload.Table;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeEventHandler;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifierManager;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class LootDisplays {
    public static final ResourceLocation BARTERING = BuiltInLootTables.PIGLIN_BARTERING.location();
    public static final String SLUICE_NAMESPACE = "spelunkery";
    public static final String SLUICE_PREFIX = "gameplay/sluice/";
    public static final ResourceLocation SNIFFER_DIGGING = BuiltInLootTables.SNIFFER_DIGGING.location();
    public static final String SLUICING = "sluicing";
    public static final String BARTERING_KIND = "bartering";
    public static final String SNIFFER = "sniffer";
    public static final String ARCHAEOLOGY = "archaeology";
    public static final String STRUCTURE_LOOT = "structure";
    private static final int SAMPLES = 16;
    private static final int MAX_DEPTH = 4;
    private static final Map<String, ResourceLocation> ITEM_MODIFIERS = Map.of(
            "modestmining:copper_screw", ResourceLocation.fromNamespaceAndPath("modestmining", "copper_screw"));

    private static ReloadableServerRegistries.Holder builtFor;
    private static LootDisplayPayload built;
    private static List<Table> received = List.of();
    private static ResourceManager indexFor;
    private static Set<ResourceLocation> indexKeys;
    private static CompletableFuture<StructureLootIndex.Index> index;

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        LootDisplayPayload payload = build(event.getPlayerList().getServer());
        event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        Set<ResourceLocation> keys = Set.copyOf(server.reloadableRegistries().getKeys(Registries.LOOT_TABLE));
        ResourceManager resources = server.getResourceManager();
        CompletableFuture<StructureLootIndex.Index> future = CompletableFuture.supplyAsync(() -> StructureLootIndex.build(server, keys), Util.backgroundExecutor());
        synchronized (LootDisplays.class) {
            indexFor = resources;
            indexKeys = keys;
            index = future;
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        synchronized (LootDisplays.class) {
            builtFor = null;
            built = null;
            indexFor = null;
            indexKeys = null;
            index = null;
        }
    }

    private static StructureLootIndex.Index structureIndex(MinecraftServer server, Set<ResourceLocation> keys) {
        if (index != null && indexFor == server.getResourceManager() && keys.equals(indexKeys)) {
            try {
                return index.join();
            } catch (RuntimeException e) {
                SpicedCider.LOGGER.warn("Structure loot index failed, rebuilding", e);
            }
        }
        return StructureLootIndex.build(server, keys);
    }

    public static void receive(LootDisplayPayload payload) {
        received = payload.tables();
    }

    public static void clearReceived() {
        received = List.of();
    }

    public static List<Table> tables() {
        return received;
    }

    private static synchronized LootDisplayPayload build(MinecraftServer server) {
        ReloadableServerRegistries.Holder holder = server.reloadableRegistries();
        if (holder == builtFor && built != null) return built;
        long start = System.nanoTime();

        Sampler sampler = new Sampler(server.overworld(), holder);
        Map<ResourceLocation, Set<ResourceLocation>> injections = injections(sampler.ops, sampler.keys);
        additions = additions(sampler.ops);
        List<Table> tables = new ArrayList<>();

        sampler.keys.stream()
                .filter(id -> id.getNamespace().equals(SLUICE_NAMESPACE) && id.getPath().startsWith(SLUICE_PREFIX))
                .sorted()
                .forEach(id -> addTable(tables, SLUICING, id, List.of(), sampler, injections, List.of(id)));
        addTable(tables, BARTERING_KIND, BARTERING, List.of(), sampler, injections, List.of(BARTERING));
        addTable(tables, SNIFFER, SNIFFER_DIGGING, List.of(), sampler, injections, List.of(SNIFFER_DIGGING));

        StructureLootIndex.Index index = structureIndex(server, sampler.keys);
        index.archaeology().forEach((structure, sources) -> addStructure(tables, ARCHAEOLOGY, structure, sources, sampler, injections));
        index.chests().forEach((structure, sources) -> addStructure(tables, STRUCTURE_LOOT, structure, sources, sampler, injections));

        builtFor = holder;
        built = new LootDisplayPayload(tables);
        SpicedCider.LOGGER.info("Built {} loot displays in {} ms", tables.size(), (System.nanoTime() - start) / 1_000_000);
        return built;
    }

    private static void addStructure(List<Table> tables, String kind, ResourceLocation structure, Set<StructureLootIndex.Source> sources,
                                     Sampler sampler, Map<ResourceLocation, Set<ResourceLocation>> injections) {
        List<ResourceLocation> inputs = sources.stream().map(StructureLootIndex.Source::block).filter(Objects::nonNull).distinct().toList();
        List<ResourceLocation> ids = sources.stream().map(StructureLootIndex.Source::table).distinct().toList();
        addTable(tables, kind, structure, inputs, sampler, injections, ids);
    }

    private static void addTable(List<Table> tables, String kind, ResourceLocation id, List<ResourceLocation> inputs, Sampler sampler,
                                 Map<ResourceLocation, Set<ResourceLocation>> injections, List<ResourceLocation> sources) {
        if (sources.stream().noneMatch(sampler.keys::contains)) return;
        List<ResourceLocation> all = new ArrayList<>();
        for (ResourceLocation source : sources) {
            all.add(source);
            all.addAll(injections.getOrDefault(source, Set.of()));
        }
        List<Drop> drops = new ArrayList<>(sampler.drops(all));
        if (!drops.isEmpty()) {
            all.stream().flatMap(source -> additions.getOrDefault(source, Set.of()).stream()).distinct().forEach(itemId -> {
                Item item = BuiltInRegistries.ITEM.get(itemId);
                if (item != Items.AIR && drops.stream().noneMatch(drop -> drop.stack().is(item))) {
                    drops.add(new Drop(new ItemStack(item), 1, 1, List.of(), List.of()));
                }
            });
            tables.add(new Table(kind, id, inputs, drops));
        }
    }

    private static Map<ResourceLocation, Set<ResourceLocation>> additions = Map.of();

    private static Map<ResourceLocation, Set<ResourceLocation>> additions(RegistryOps<JsonElement> ops) {
        Map<ResourceLocation, Set<ResourceLocation>> found = new HashMap<>();
        LootModifierManager manager = lootModifierManager();
        if (manager == null) return found;
        for (IGlobalLootModifier modifier : manager.getAllLootMods()) {
            JsonElement json;
            try {
                json = IGlobalLootModifier.DIRECT_CODEC.encodeStart(ops, modifier).result().orElse(null);
            } catch (RuntimeException e) {
                continue;
            }
            if (!(json instanceof JsonObject object)) continue;
            ResourceLocation item = ITEM_MODIFIERS.get(string(object.get("type")));
            if (item == null) continue;
            for (ResourceLocation target : targets(object.get("conditions"))) {
                found.computeIfAbsent(target, key -> new LinkedHashSet<>()).add(item);
            }
        }
        return found;
    }

    private static Map<ResourceLocation, Set<ResourceLocation>> injections(RegistryOps<JsonElement> ops, Set<ResourceLocation> keys) {
        Map<ResourceLocation, Set<ResourceLocation>> found = new HashMap<>();
        LootModifierManager manager = lootModifierManager();
        if (manager == null) return found;
        for (IGlobalLootModifier modifier : manager.getAllLootMods()) {
            JsonElement json;
            try {
                json = IGlobalLootModifier.DIRECT_CODEC.encodeStart(ops, modifier).result().orElse(null);
            } catch (RuntimeException e) {
                continue;
            }
            if (!(json instanceof JsonObject object)) continue;
            Set<ResourceLocation> targets = targets(object.get("conditions"));
            if (targets.isEmpty()) continue;
            Set<ResourceLocation> injected = new LinkedHashSet<>();
            for (Map.Entry<String, JsonElement> field : object.entrySet()) {
                if (!field.getKey().equals("conditions") && !field.getKey().equals("type")) {
                    collectTableIds(field.getValue(), keys, injected);
                }
            }
            injected.removeAll(targets);
            if (injected.isEmpty()) continue;
            for (ResourceLocation target : targets) found.computeIfAbsent(target, key -> new LinkedHashSet<>()).addAll(injected);
        }
        return found;
    }

    private static LootModifierManager lootModifierManager() {
        try {
            Method method = NeoForgeEventHandler.class.getDeclaredMethod("getLootModifierManager");
            method.setAccessible(true);
            return (LootModifierManager) method.invoke(null);
        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }

    private static Set<ResourceLocation> targets(JsonElement conditions) {
        Set<ResourceLocation> targets = new LinkedHashSet<>();
        if (!(conditions instanceof JsonArray array)) return targets;
        for (JsonElement element : array) {
            collectTargets(element, targets);
        }
        return targets;
    }

    private static void collectTargets(JsonElement element, Set<ResourceLocation> targets) {
        if (!(element instanceof JsonObject condition)) return;
        String type = string(condition.get("condition"));
        if ("neoforge:loot_table_id".equals(type)) {
            ResourceLocation id = ResourceLocation.tryParse(string(condition.get("loot_table_id")));
            if (id != null) targets.add(id);
        } else if (("minecraft:any_of".equals(type) || "minecraft:all_of".equals(type)) && condition.get("terms") instanceof JsonArray terms) {
            terms.forEach(term -> collectTargets(term, targets));
        }
    }

    private static void collectTableIds(JsonElement element, Set<ResourceLocation> keys, Set<ResourceLocation> found) {
        if (element instanceof JsonObject object) {
            object.entrySet().forEach(entry -> collectTableIds(entry.getValue(), keys, found));
        } else if (element instanceof JsonArray array) {
            array.forEach(child -> collectTableIds(child, keys, found));
        } else if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            ResourceLocation id = ResourceLocation.tryParse(element.getAsString());
            if (id != null && keys.contains(id)) found.add(id);
        }
    }

    private static String string(JsonElement element) {
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    private static String type(JsonObject object, String field) {
        String type = string(object.get(field));
        if (type == null) return "";
        return type.startsWith("minecraft:") ? type.substring("minecraft:".length()) : type;
    }

    private static List<JsonElement> plus(List<JsonElement> conditions, JsonElement more) {
        if (!(more instanceof JsonArray array) || array.isEmpty()) return conditions;
        List<JsonElement> combined = new ArrayList<>(conditions);
        array.forEach(combined::add);
        return combined;
    }

    private record StackKey(Item item, Object potion) {
        static StackKey of(ItemStack stack) {
            return new StackKey(stack.getItem(), stack.get(DataComponents.POTION_CONTENTS));
        }
    }

    private record Weighted(int weight, int order, Drop drop) {
    }

    private static class Sampler {
        final ServerLevel level;
        final ReloadableServerRegistries.Holder holder;
        final RegistryOps<JsonElement> ops;
        final Set<ResourceLocation> keys;
        final LootParams params;
        final Set<String> disabledStructures;
        final Map<ResourceLocation, List<Weighted>> tableCache = new HashMap<>();

        Sampler(ServerLevel level, ReloadableServerRegistries.Holder holder) {
            this.level = level;
            this.holder = holder;
            this.ops = holder.get().createSerializationContext(JsonOps.INSTANCE);
            this.keys = Set.copyOf(holder.getKeys(Registries.LOOT_TABLE));
            this.params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.ZERO).create(LootContextParamSets.CHEST);
            this.disabledStructures = new HashSet<>();
            level.registryAccess().registryOrThrow(Registries.STRUCTURE).getTagOrEmpty(StructureLootIndex.DISABLED)
                    .forEach(structure -> structure.unwrapKey().ifPresent(key -> disabledStructures.add(key.location().toString())));
        }

        List<Drop> drops(List<ResourceLocation> ids) {
            List<Weighted> weighted = new ArrayList<>();
            for (ResourceLocation id : ids) {
                weighted.addAll(tableCache.computeIfAbsent(id, key -> {
                    List<Weighted> entries = new ArrayList<>();
                    if (encode(key) instanceof JsonObject object) walkTable(object, List.of(), entries, 0);
                    return entries;
                }));
            }
            weighted.sort(Comparator.comparingInt(Weighted::weight).reversed().thenComparingInt(Weighted::order));
            Map<StackKey, Drop> unique = new LinkedHashMap<>();
            for (Weighted entry : weighted) {
                unique.putIfAbsent(StackKey.of(entry.drop().stack()), entry.drop());
            }
            return List.copyOf(unique.values());
        }

        JsonElement encode(ResourceLocation id) {
            LootTable table = holder.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
            if (table == LootTable.EMPTY) return null;
            try {
                return LootTable.DIRECT_CODEC.encodeStart(ops, table).result().orElse(null);
            } catch (RuntimeException e) {
                return null;
            }
        }

        void walkTable(JsonObject table, List<JsonElement> conditions, List<Weighted> out, int depth) {
            if (!(table.get("pools") instanceof JsonArray pools)) return;
            for (JsonElement poolElement : pools) {
                if (!(poolElement instanceof JsonObject pool) || !(pool.get("entries") instanceof JsonArray entries)) continue;
                List<JsonElement> poolConditions = plus(conditions, pool.get("conditions"));
                for (JsonElement entry : entries) {
                    if (entry instanceof JsonObject object) walkEntry(object, poolConditions, out, depth);
                }
            }
        }

        void walkEntry(JsonObject entry, List<JsonElement> inherited, List<Weighted> out, int depth) {
            List<JsonElement> conditions = plus(inherited, entry.get("conditions"));
            switch (type(entry, "type")) {
                case "alternatives", "group", "sequence" -> {
                    if (entry.get("children") instanceof JsonArray children) {
                        for (JsonElement child : children) {
                            if (child instanceof JsonObject object) walkEntry(object, conditions, out, depth);
                        }
                    }
                }
                case "loot_table" -> {
                    if (depth >= MAX_DEPTH) return;
                    JsonElement value = entry.get("value");
                    if (value instanceof JsonObject inline) {
                        walkTable(inline, conditions, out, depth + 1);
                    } else if (string(value) != null) {
                        ResourceLocation id = ResourceLocation.tryParse(string(value));
                        if (id != null && encode(id) instanceof JsonObject referenced) walkTable(referenced, conditions, out, depth + 1);
                    }
                }
                case "empty" -> {
                }
                default -> sample(entry, conditions, out);
            }
        }

        void sample(JsonObject entry, List<JsonElement> conditions, List<Weighted> out) {
            JsonObject single = entry.deepCopy();
            single.remove("conditions");
            if (single.get("functions") instanceof JsonArray functions) {
                JsonArray kept = new JsonArray();
                for (JsonElement function : functions) {
                    if (!(function instanceof JsonObject object) || !type(object, "function").endsWith("exploration_map")) kept.add(function);
                }
                if (kept.isEmpty()) single.remove("functions");
                else single.add("functions", kept);
            }
            boolean deterministic = !single.has("functions") && "item".equals(type(single, "type"));
            JsonObject pool = new JsonObject();
            pool.addProperty("rolls", 1);
            JsonArray entries = new JsonArray();
            entries.add(single);
            pool.add("entries", entries);
            JsonArray pools = new JsonArray();
            pools.add(pool);
            JsonObject tableJson = new JsonObject();
            tableJson.add("pools", pools);

            Map<StackKey, ItemStack> stacks = new LinkedHashMap<>();
            Map<StackKey, int[]> counts = new LinkedHashMap<>();
            LootTable table = LootTable.DIRECT_CODEC.parse(ops, tableJson).result().orElse(null);
            if (table != null) {
                for (int i = 0; i < (deterministic ? 1 : SAMPLES); i++) {
                    Map<StackKey, Integer> roll = new LinkedHashMap<>();
                    try {
                        for (ItemStack stack : table.getRandomItems(params, i)) {
                            if (stack.isEmpty()) continue;
                            StackKey key = StackKey.of(stack);
                            stacks.putIfAbsent(key, stack.copyWithCount(1));
                            roll.merge(key, stack.getCount(), Integer::sum);
                        }
                    } catch (RuntimeException e) {
                        break;
                    }
                    roll.forEach((key, count) -> {
                        int[] range = counts.computeIfAbsent(key, k -> new int[]{count, count});
                        range[0] = Math.min(range[0], count);
                        range[1] = Math.max(range[1], count);
                    });
                }
            }
            if (stacks.isEmpty() && "item".equals(type(entry, "type")) && string(entry.get("name")) != null) {
                ResourceLocation id = ResourceLocation.tryParse(string(entry.get("name")));
                Item item = id == null ? Items.AIR : BuiltInRegistries.ITEM.get(id);
                if (item != Items.AIR) {
                    ItemStack stack = new ItemStack(item);
                    StackKey key = StackKey.of(stack);
                    stacks.put(key, stack);
                    counts.put(key, new int[]{1, 1});
                }
            }

            int weight = entry.has("weight") ? entry.get("weight").getAsInt() : 1;
            Set<String> biomes = new LinkedHashSet<>();
            Set<String> structures = new LinkedHashSet<>();
            conditions.forEach(condition -> describe(condition, biomes, structures));
            boolean structureBound = !structures.isEmpty();
            structures.removeIf(disabledStructures::contains);
            if (structureBound && structures.isEmpty() && biomes.isEmpty()) return;
            stacks.forEach((key, stack) -> {
                int[] range = Objects.requireNonNullElse(counts.get(key), new int[]{1, 1});
                out.add(new Weighted(weight, out.size(), new Drop(stack, range[0], range[1], List.copyOf(biomes), List.copyOf(structures))));
            });
        }

        void describe(JsonElement element, Set<String> biomes, Set<String> structures) {
            if (!(element instanceof JsonObject condition)) return;
            switch (type(condition, "condition")) {
                case "any_of", "all_of" -> {
                    if (condition.get("terms") instanceof JsonArray terms) terms.forEach(term -> describe(term, biomes, structures));
                }
                case "location_check" -> {
                    if (condition.get("predicate") instanceof JsonObject predicate) {
                        addAll(predicate.get("biomes"), biomes);
                        addAll(predicate.get("structures"), structures);
                    }
                }
                default -> {
                }
            }
        }

        void addAll(JsonElement element, Set<String> into) {
            if (element instanceof JsonArray array) {
                array.forEach(child -> addAll(child, into));
            } else if (string(element) != null) {
                into.add(string(element));
            }
        }
    }
}

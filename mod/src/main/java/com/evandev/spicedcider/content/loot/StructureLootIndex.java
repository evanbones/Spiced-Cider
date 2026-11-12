package com.evandev.spicedcider.content.loot;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StructureLootIndex {
    public static final TagKey<Structure> DISABLED = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "disabled"));
    public static final ResourceLocation DUNGEON = ResourceLocation.withDefaultNamespace("monster_room");
    public static final ResourceLocation DESERT_WELL = ResourceLocation.withDefaultNamespace("desert_well");
    private static final String POT_FEATURE = "nomansland:pot_patch";
    private static final ResourceLocation POT_ICON = ResourceLocation.fromNamespaceAndPath("nomansland", "living_urn");
    private static final List<String> IGNORED_PREFIXES = List.of("entities/", "blocks/", "gameplay/", "equipment/", "spawners/", "shearing/", "pots/");

    public record Source(ResourceLocation table, @Nullable ResourceLocation block) {
        boolean archaeology() {
            if (block != null && BuiltInRegistries.BLOCK.get(block) instanceof BrushableBlock) return true;
            return block == null && table.getPath().contains("archaeology");
        }
    }

    public record Index(Map<ResourceLocation, Set<Source>> chests, Map<ResourceLocation, Set<Source>> archaeology) {
    }

    private record TemplateScan(Set<Source> sources, Set<ResourceLocation> pools) {
    }

    private final MinecraftServer server;
    private final Set<ResourceLocation> lootKeys;
    private final RegistryOps<JsonElement> ops;
    private final Registry<StructureTemplatePool> pools;
    private final Registry<StructureProcessorList> processors;
    private final Registry<PlacedFeature> placedFeatures;
    private final Registry<ConfiguredFeature<?, ?>> configuredFeatures;
    private final Map<ResourceLocation, Set<Source>> featureScans = new HashMap<>();
    private final Map<ResourceLocation, TemplateScan> templates = new HashMap<>();
    private final Map<ResourceLocation, Set<Source>> processorScans = new HashMap<>();
    private final Map<ResourceLocation, JsonElement> poolJson = new HashMap<>();

    private StructureLootIndex(MinecraftServer server, Set<ResourceLocation> lootKeys) {
        this.server = server;
        this.lootKeys = lootKeys;
        RegistryAccess access = server.registryAccess();
        this.ops = access.createSerializationContext(JsonOps.INSTANCE);
        this.pools = access.registryOrThrow(Registries.TEMPLATE_POOL);
        this.processors = access.registryOrThrow(Registries.PROCESSOR_LIST);
        this.placedFeatures = access.registryOrThrow(Registries.PLACED_FEATURE);
        this.configuredFeatures = access.registryOrThrow(Registries.CONFIGURED_FEATURE);
    }

    public static Index build(MinecraftServer server, Set<ResourceLocation> lootKeys) {
        return new StructureLootIndex(server, lootKeys).run();
    }

    private Index run() {
        Map<ResourceLocation, Set<Source>> chests = new LinkedHashMap<>();
        Map<ResourceLocation, Set<Source>> archaeology = new LinkedHashMap<>();
        RegistryAccess access = server.registryAccess();
        Registry<Structure> structures = access.registryOrThrow(Registries.STRUCTURE);

        Set<Structure> placed = new HashSet<>();
        for (StructureSet set : access.registryOrThrow(Registries.STRUCTURE_SET)) {
            for (StructureSet.StructureSelectionEntry entry : set.structures()) placed.add(entry.structure().value());
        }

        structures.holders().sorted((a, b) -> a.key().location().compareTo(b.key().location())).forEach(holder -> {
            Structure structure = holder.value();
            if (!placed.contains(structure) || holder.is(DISABLED) || endOnly(structure.biomes())) return;
            Set<Source> sources = new LinkedHashSet<>();
            JsonElement json = encode(Structure.DIRECT_CODEC, structure);
            ResourceLocation type = BuiltInRegistries.STRUCTURE_TYPE.getKey(structure.type());
            if (type != null) hardcoded(type, json, sources);
            if (json != null) walkStructure(json, sources);
            add(holder.key().location(), sources, chests, archaeology);
        });

        features(access, chests, archaeology);
        return new Index(chests, archaeology);
    }

    private void add(ResourceLocation id, Set<Source> sources, Map<ResourceLocation, Set<Source>> chests, Map<ResourceLocation, Set<Source>> archaeology) {
        for (Source source : sources) {
            if (!lootKeys.contains(source.table())) continue;
            String path = source.table().getPath();
            if (IGNORED_PREFIXES.stream().anyMatch(path::startsWith)) continue;
            (source.archaeology() ? archaeology : chests).computeIfAbsent(id, key -> new LinkedHashSet<>()).add(source);
        }
    }

    private static boolean endOnly(HolderSet<Biome> biomes) {
        return biomes.size() > 0 && biomes.stream().allMatch(biome -> biome.is(BiomeTags.IS_END));
    }

    private void features(RegistryAccess access, Map<ResourceLocation, Set<Source>> chests, Map<ResourceLocation, Set<Source>> archaeology) {
        boolean dungeon = false;
        boolean well = false;
        for (Biome biome : access.registryOrThrow(Registries.BIOME)) {
            for (HolderSet<PlacedFeature> step : biome.getGenerationSettings().features()) {
                for (Holder<PlacedFeature> placed : step) {
                    Feature<?> feature = placed.value().feature().value().feature();
                    if (feature == Feature.MONSTER_ROOM) dungeon = true;
                    if (feature == Feature.DESERT_WELL) well = true;
                }
            }
        }
        if (dungeon) add(DUNGEON, Set.of(new Source(vanilla("chests/simple_dungeon"), vanilla("chest"))), chests, archaeology);
        if (well) add(DESERT_WELL, Set.of(new Source(vanilla("archaeology/desert_well"), vanilla("suspicious_sand"))), chests, archaeology);
    }

    private static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    private void hardcoded(ResourceLocation type, @Nullable JsonElement json, Set<Source> sources) {
        ResourceLocation chest = vanilla("chest");
        if (type.toString().equals("bettermineshafts:mineshaft")) {
            sources.add(new Source(vanilla("chests/abandoned_mineshaft"), vanilla("chest_minecart")));
            return;
        }
        if (!type.getNamespace().equals("minecraft")) return;
        switch (type.getPath()) {
            case "mineshaft" -> sources.add(new Source(vanilla("chests/abandoned_mineshaft"), vanilla("chest_minecart")));
            case "desert_pyramid" -> {
                sources.add(new Source(vanilla("chests/desert_pyramid"), chest));
                sources.add(new Source(vanilla("archaeology/desert_pyramid"), vanilla("suspicious_sand")));
            }
            case "jungle_temple" -> {
                sources.add(new Source(vanilla("chests/jungle_temple"), chest));
                sources.add(new Source(vanilla("chests/jungle_temple_dispenser"), vanilla("dispenser")));
            }
            case "stronghold" -> {
                sources.add(new Source(vanilla("chests/stronghold_corridor"), chest));
                sources.add(new Source(vanilla("chests/stronghold_crossing"), chest));
                sources.add(new Source(vanilla("chests/stronghold_library"), chest));
            }
            case "shipwreck" -> {
                sources.add(new Source(vanilla("chests/shipwreck_supply"), chest));
                sources.add(new Source(vanilla("chests/shipwreck_map"), chest));
                sources.add(new Source(vanilla("chests/shipwreck_treasure"), chest));
            }
            case "ocean_ruin" -> {
                sources.add(new Source(vanilla("chests/underwater_ruin_small"), chest));
                sources.add(new Source(vanilla("chests/underwater_ruin_big"), chest));
                boolean cold = json instanceof JsonObject object && object.has("biome_temp") && "cold".equals(object.get("biome_temp").getAsString());
                sources.add(cold
                        ? new Source(vanilla("archaeology/ocean_ruin_cold"), vanilla("suspicious_gravel"))
                        : new Source(vanilla("archaeology/ocean_ruin_warm"), vanilla("suspicious_sand")));
            }
            case "buried_treasure" -> sources.add(new Source(vanilla("chests/buried_treasure"), chest));
            case "woodland_mansion" -> sources.add(new Source(vanilla("chests/woodland_mansion"), chest));
            case "fortress" -> sources.add(new Source(vanilla("chests/nether_bridge"), chest));
            case "end_city" -> sources.add(new Source(vanilla("chests/end_city_treasure"), chest));
            case "igloo" -> sources.add(new Source(vanilla("chests/igloo_chest"), chest));
            case "ruined_portal" -> sources.add(new Source(vanilla("chests/ruined_portal"), chest));
            case "ocean_monument" -> {
                if (SpicedCiderConfig.feature("monumentChests")) {
                    sources.add(new Source(ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "chests/ocean_monument"),
                            ResourceLocation.fromNamespaceAndPath("quark", "prismarine_chest")));
                }
            }
            default -> {
            }
        }
    }

    private void walkStructure(JsonElement json, Set<Source> sources) {
        Deque<ResourceLocation> queue = new ArrayDeque<>();
        Set<ResourceLocation> visited = new HashSet<>();
        scanJson(json, sources, queue);
        while (!queue.isEmpty()) {
            ResourceLocation pool = queue.poll();
            if (!visited.add(pool)) continue;
            JsonElement element = poolJson.computeIfAbsent(pool, id -> pools.getOptional(id).map(value -> encode(StructureTemplatePool.DIRECT_CODEC, value)).orElse(null));
            if (element != null) scanJson(element, sources, queue);
        }
    }

    private void scanJson(JsonElement element, Set<Source> sources, Deque<ResourceLocation> queue) {
        if (element instanceof JsonObject object) {
            if (object.get("location") instanceof JsonElement location && location.isJsonPrimitive() && object.has("element_type")) {
                ResourceLocation id = ResourceLocation.tryParse(location.getAsString());
                if (id != null) {
                    TemplateScan scan = template(id);
                    sources.addAll(scan.sources());
                    queue.addAll(scan.pools());
                }
            }
            JsonElement processorsElement = object.get("processors");
            if (processorsElement != null && processorsElement.isJsonPrimitive()) {
                ResourceLocation id = ResourceLocation.tryParse(processorsElement.getAsString());
                if (id != null) sources.addAll(processorList(id));
            }
            if (object.get("output_state") instanceof JsonObject output) {
                ResourceLocation block = output.has("Name") ? ResourceLocation.tryParse(output.get("Name").getAsString()) : null;
                Set<ResourceLocation> tables = new LinkedHashSet<>();
                collectLootStrings(object, tables);
                tables.forEach(table -> sources.add(new Source(table, block)));
                return;
            }
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (!entry.getKey().equals("location")) scanJson(entry.getValue(), sources, queue);
            }
        } else if (element instanceof JsonArray array) {
            array.forEach(child -> scanJson(child, sources, queue));
        } else if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            ResourceLocation id = ResourceLocation.tryParse(element.getAsString());
            if (id == null) return;
            if (lootKeys.contains(id)) sources.add(new Source(id, null));
            else if (pools.containsKey(id)) queue.add(id);
            else if (placedFeatures.containsKey(id) || configuredFeatures.containsKey(id)) sources.addAll(feature(id));
        }
    }

    private void collectLootStrings(JsonElement element, Set<ResourceLocation> into) {
        if (element instanceof JsonObject object) {
            object.entrySet().forEach(entry -> collectLootStrings(entry.getValue(), into));
        } else if (element instanceof JsonArray array) {
            array.forEach(child -> collectLootStrings(child, into));
        } else if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            ResourceLocation id = ResourceLocation.tryParse(element.getAsString());
            if (id != null && lootKeys.contains(id)) into.add(id);
        }
    }

    private Set<Source> feature(ResourceLocation id) {
        return featureScans.computeIfAbsent(id, key -> {
            Set<Source> found = new LinkedHashSet<>();
            JsonElement json = placedFeatures.getOptional(key).map(value -> encode(PlacedFeature.DIRECT_CODEC, value))
                    .orElseGet(() -> configuredFeatures.getOptional(key).map(value -> encode(ConfiguredFeature.DIRECT_CODEC, value)).orElse(null));
            if (json == null) return found;
            Set<ResourceLocation> tables = new LinkedHashSet<>();
            boolean pots = collectFeatureLoot(json, tables, new HashSet<>());
            ResourceLocation icon = pots && BuiltInRegistries.ITEM.containsKey(POT_ICON) ? POT_ICON : null;
            tables.forEach(table -> found.add(new Source(table, icon)));
            return found;
        });
    }

    private boolean collectFeatureLoot(JsonElement element, Set<ResourceLocation> tables, Set<ResourceLocation> visited) {
        boolean pots = false;
        if (element instanceof JsonObject object) {
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) pots |= collectFeatureLoot(entry.getValue(), tables, visited);
        } else if (element instanceof JsonArray array) {
            for (JsonElement child : array) pots |= collectFeatureLoot(child, tables, visited);
        } else if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String text = element.getAsString();
            if (text.equals(POT_FEATURE)) return true;
            ResourceLocation id = ResourceLocation.tryParse(text);
            if (id == null) return false;
            if (lootKeys.contains(id)) {
                tables.add(id);
            } else if (configuredFeatures.containsKey(id) && visited.add(id)) {
                JsonElement json = configuredFeatures.getOptional(id).map(value -> encode(ConfiguredFeature.DIRECT_CODEC, value)).orElse(null);
                if (json != null) pots |= collectFeatureLoot(json, tables, visited);
            }
        }
        return pots;
    }

    private Set<Source> processorList(ResourceLocation id) {
        return processorScans.computeIfAbsent(id, key -> {
            Set<Source> sources = new LinkedHashSet<>();
            processors.getOptional(key).map(list -> encode(StructureProcessorType.DIRECT_CODEC, list))
                    .ifPresent(json -> scanJson(json, sources, new ArrayDeque<>()));
            return sources;
        });
    }

    private TemplateScan template(ResourceLocation id) {
        return templates.computeIfAbsent(id, key -> {
            Set<Source> sources = new LinkedHashSet<>();
            Set<ResourceLocation> jigsawPools = new LinkedHashSet<>();
            CompoundTag tag = readTemplate(key);
            if (tag == null) return new TemplateScan(sources, jigsawPools);
            ListTag palette = tag.getList("palette", Tag.TAG_COMPOUND);
            if (palette.isEmpty()) {
                ListTag palettes = tag.getList("palettes", Tag.TAG_LIST);
                if (!palettes.isEmpty()) palette = palettes.getList(0);
            }
            ListTag blocks = tag.getList("blocks", Tag.TAG_COMPOUND);
            for (int i = 0; i < blocks.size(); i++) {
                CompoundTag block = blocks.getCompound(i);
                if (!block.contains("nbt", Tag.TAG_COMPOUND)) continue;
                CompoundTag nbt = block.getCompound("nbt");
                int state = block.getInt("state");
                ResourceLocation blockId = containerBlock(state < palette.size() ? ResourceLocation.tryParse(palette.getCompound(state).getString("Name")) : null, nbt);
                if (nbt.contains("pool", Tag.TAG_STRING)) {
                    ResourceLocation pool = ResourceLocation.tryParse(nbt.getString("pool"));
                    if (pool != null) jigsawPools.add(pool);
                }
                Set<ResourceLocation> tables = new LinkedHashSet<>();
                collectLootStrings(nbt, tables);
                tables.forEach(table -> sources.add(new Source(table, blockId)));
            }
            ListTag entities = tag.getList("entities", Tag.TAG_COMPOUND);
            for (int i = 0; i < entities.size(); i++) {
                CompoundTag nbt = entities.getCompound(i).getCompound("nbt");
                Set<ResourceLocation> tables = new LinkedHashSet<>();
                collectLootStrings(nbt, tables);
                ResourceLocation entityId = ResourceLocation.tryParse(nbt.getString("id"));
                ResourceLocation item = entityId != null && BuiltInRegistries.ITEM.containsKey(entityId) ? entityId : null;
                tables.forEach(table -> sources.add(new Source(table, item)));
            }
            return new TemplateScan(sources, jigsawPools);
        });
    }

    private void collectLootStrings(Tag tag, Set<ResourceLocation> into) {
        if (tag instanceof CompoundTag compound) {
            for (String key : compound.getAllKeys()) collectLootStrings(compound.get(key), into);
        } else if (tag instanceof ListTag list) {
            list.forEach(child -> collectLootStrings(child, into));
        } else if (tag instanceof StringTag string) {
            ResourceLocation id = ResourceLocation.tryParse(string.getAsString());
            if (id != null && lootKeys.contains(id)) into.add(id);
        }
    }

    @Nullable
    private static ResourceLocation containerBlock(@Nullable ResourceLocation block, CompoundTag nbt) {
        if (block == null) return null;
        String id = block.toString();
        if (id.equals("minecraft:jigsaw")) {
            String finalState = nbt.getString("final_state");
            int end = finalState.length();
            for (char stop : new char[]{'[', '{'}) {
                int index = finalState.indexOf(stop);
                if (index >= 0) end = Math.min(end, index);
            }
            ResourceLocation state = ResourceLocation.tryParse(finalState.substring(0, end).trim());
            return state == null || state.getPath().equals("air") || state.getPath().equals("structure_void") ? null : state;
        }
        if (id.equals("minecraft:structure_block") || id.equals("minecraft:structure_void") || id.endsWith("command_block")) return null;
        return block;
    }

    @Nullable
    private CompoundTag readTemplate(ResourceLocation id) {
        ResourceLocation file = id.withPath(path -> "structure/" + path + ".nbt");
        Optional<Resource> resource = server.getResourceManager().getResource(file);
        if (resource.isEmpty()) return null;
        try (InputStream stream = resource.get().open()) {
            return NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private <T> JsonElement encode(Codec<T> codec, T value) {
        try {
            return codec.encodeStart(ops, value).result().orElse(null);
        } catch (RuntimeException e) {
            return null;
        }
    }
}

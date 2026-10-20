package com.evandev.spicedcider.util;

import com.evandev.spicedcider.SpicedCider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class ToolStats {

    public record Stats(int durability, Optional<Float> speed) {
    }

    private record Entry(Stats stats, Set<ResourceLocation> ids, List<Pattern> patterns) {
    }

    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("spicedcider/tool_stats.json");
    private static ToolStats instance;

    private final Map<ResourceLocation, Stats> overrides = new HashMap<>();
    private final List<Entry> tools = new ArrayList<>();
    private final List<Entry> armor = new ArrayList<>();
    private final Set<ResourceLocation> requiredIds = new HashSet<>();
    private final Set<ResourceLocation> optionalIds = new HashSet<>();

    private ToolStats() {
    }

    public static ToolStats get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public boolean isEmpty() {
        return overrides.isEmpty() && tools.isEmpty() && armor.isEmpty();
    }

    public Optional<Stats> lookup(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        Stats override = overrides.get(id);
        if (override != null) return Optional.of(override);

        List<Entry> section = item instanceof ArmorItem || item instanceof AnimalArmorItem ? armor : tools;
        for (Entry entry : section) {
            if (entry.ids().contains(id)) return Optional.of(entry.stats());
        }
        String text = id.toString();
        for (Entry entry : section) {
            for (Pattern pattern : entry.patterns()) {
                if (pattern.matcher(text).matches()) return Optional.of(entry.stats());
            }
        }
        return Optional.empty();
    }

    public void reportMissingIds() {
        for (ResourceLocation id : requiredIds) {
            if (!BuiltInRegistries.ITEM.containsKey(id)) {
                SpicedCider.LOGGER.warn("Tool stats: unknown item {}", id);
            }
        }
        for (ResourceLocation id : optionalIds) {
            if (!BuiltInRegistries.ITEM.containsKey(id)) {
                SpicedCider.LOGGER.debug("Tool stats: optional item {} is not present", id);
            }
        }
    }

    private static ToolStats load() {
        ToolStats stats = new ToolStats();
        if (!Files.isRegularFile(FILE)) {
            SpicedCider.LOGGER.debug("Tool stats: {} not found", FILE);
            return stats;
        }
        try {
            JsonObject root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8)).getAsJsonObject();
            stats.readSection(root, "tools", stats.tools, true);
            stats.readSection(root, "armor", stats.armor, false);
            if (root.has("items")) {
                for (Map.Entry<String, JsonElement> e : root.getAsJsonObject("items").entrySet()) {
                    String key = e.getKey();
                    boolean optional = key.startsWith("?");
                    ResourceLocation id = ResourceLocation.tryParse(optional ? key.substring(1) : key);
                    if (id == null) {
                        SpicedCider.LOGGER.warn("Tool stats: invalid item id {}", key);
                        continue;
                    }
                    (optional ? stats.optionalIds : stats.requiredIds).add(id);
                    stats.overrides.put(id, readStats(e.getValue().getAsJsonObject(), true));
                }
            }
        } catch (IOException | RuntimeException e) {
            SpicedCider.LOGGER.error("Tool stats: failed to read {}", FILE, e);
            return new ToolStats();
        }
        return stats;
    }

    private void readSection(JsonObject root, String name, List<Entry> into, boolean allowSpeed) {
        if (!root.has(name)) return;
        for (Map.Entry<String, JsonElement> e : root.getAsJsonObject(name).entrySet()) {
            JsonObject obj = e.getValue().getAsJsonObject();
            Set<ResourceLocation> ids = new HashSet<>();
            List<Pattern> patterns = new ArrayList<>();
            if (obj.has("items")) {
                for (JsonElement element : obj.getAsJsonArray("items")) {
                    readMatcher(name + "." + e.getKey(), element.getAsString(), ids, patterns);
                }
            }
            into.add(new Entry(readStats(obj, allowSpeed), ids, patterns));
        }
    }

    private void readMatcher(String where, String raw, Set<ResourceLocation> ids, List<Pattern> patterns) {
        boolean optional = raw.startsWith("?");
        String value = optional ? raw.substring(1) : raw;
        ResourceLocation id = value.indexOf(':') > 0 ? ResourceLocation.tryParse(value) : null;
        if (id != null) {
            ids.add(id);
            (optional ? optionalIds : requiredIds).add(id);
            return;
        }
        try {
            patterns.add(Pattern.compile(value));
        } catch (PatternSyntaxException ex) {
            SpicedCider.LOGGER.warn("Tool stats: invalid pattern {} in {}", value, where);
        }
    }

    private static Stats readStats(JsonObject obj, boolean allowSpeed) {
        int durability = obj.get("durability").getAsInt();
        Optional<Float> speed = allowSpeed && obj.has("speed") ? Optional.of(obj.get("speed").getAsFloat()) : Optional.empty();
        return new Stats(durability, speed);
    }
}

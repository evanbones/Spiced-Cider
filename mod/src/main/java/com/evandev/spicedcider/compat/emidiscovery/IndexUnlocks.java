package com.evandev.spicedcider.compat.emidiscovery;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class IndexUnlocks {

    private record Rule(ResourceLocation advancement, List<Predicate<Item>> include, List<Predicate<Item>> exclude) {
        boolean matches(Item item) {
            for (Predicate<Item> p : exclude) {
                if (p.test(item)) return false;
            }
            for (Predicate<Item> p : include) {
                if (p.test(item)) return true;
            }
            return false;
        }
    }

    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("spicedcider/index_unlocks.json");
    private static final Set<ResourceLocation> done = new HashSet<>();
    private static final Set<ResourceLocation> appliedThisSession = new HashSet<>();
    private static List<Rule> rules;
    private static ResourceLocation tabsAdvancement;
    private static boolean tabsWereLocked = true;

    public static boolean enabled() {
        return SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.emiDiscoveryIndexUnlocks, false);
    }

    public static boolean tabsLocked() {
        if (!enabled()) return false;
        load();
        return tabsAdvancement != null && !done.contains(tabsAdvancement);
    }

    public static void onAdvancementsUpdated(ClientboundUpdateAdvancementsPacket packet) {
        if (!enabled()) return;
        load();
        if (packet.shouldReset()) {
            done.clear();
            appliedThisSession.clear();
        }
        done.removeAll(packet.getRemoved());
        for (Map.Entry<ResourceLocation, AdvancementProgress> entry : packet.getProgress().entrySet()) {
            if (entry.getValue().isDone()) {
                done.add(entry.getKey());
            } else {
                done.remove(entry.getKey());
            }
        }

        if (ModList.get().isLoaded("emi_discovery")) {
            List<Item> unlocked = new ArrayList<>();
            for (Rule rule : rules) {
                if (!done.contains(rule.advancement()) || !appliedThisSession.add(rule.advancement())) continue;
                for (Item item : BuiltInRegistries.ITEM) {
                    if (rule.matches(item)) unlocked.add(item);
                }
            }
            if (!unlocked.isEmpty()) EmiDiscoveryUnlocker.unlock(unlocked);
        }

        boolean locked = tabsLocked();
        if (locked != tabsWereLocked) {
            tabsWereLocked = locked;
            if (ModList.get().isLoaded("remi")) RemiTabRefresher.refresh();
        }
    }

    private static void load() {
        if (rules != null) return;
        rules = new ArrayList<>();
        if (!Files.isRegularFile(FILE)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8)).getAsJsonObject();
            if (root.has("tabs")) tabsAdvancement = ResourceLocation.tryParse(root.get("tabs").getAsString());
            if (root.has("unlocks")) {
                for (JsonElement element : root.getAsJsonArray("unlocks")) {
                    JsonObject obj = element.getAsJsonObject();
                    ResourceLocation advancement = ResourceLocation.tryParse(obj.get("advancement").getAsString());
                    if (advancement == null) continue;
                    rules.add(new Rule(advancement, matchers(obj.getAsJsonArray("include")), matchers(obj.getAsJsonArray("exclude"))));
                }
            }
        } catch (Exception e) {
            SpicedCider.LOGGER.error("Index unlocks: failed to read {}", FILE, e);
        }
    }

    private static List<Predicate<Item>> matchers(JsonArray array) {
        List<Predicate<Item>> result = new ArrayList<>();
        if (array == null) return result;
        for (JsonElement element : array) {
            String raw = element.getAsString();
            if (raw.startsWith("#")) {
                ResourceLocation id = ResourceLocation.tryParse(raw.substring(1));
                if (id == null) continue;
                TagKey<Item> tag = TagKey.create(Registries.ITEM, id);
                result.add(item -> item.builtInRegistryHolder().is(tag));
            } else if (raw.length() > 1 && raw.startsWith("/") && raw.endsWith("/")) {
                try {
                    Pattern pattern = Pattern.compile(raw.substring(1, raw.length() - 1));
                    result.add(item -> pattern.matcher(BuiltInRegistries.ITEM.getKey(item).toString()).matches());
                } catch (PatternSyntaxException e) {
                    SpicedCider.LOGGER.warn("Index unlocks: invalid pattern {}", raw);
                }
            } else {
                ResourceLocation id = ResourceLocation.tryParse(raw);
                if (id != null) result.add(item -> BuiltInRegistries.ITEM.getKey(item).equals(id));
            }
        }
        return result;
    }
}

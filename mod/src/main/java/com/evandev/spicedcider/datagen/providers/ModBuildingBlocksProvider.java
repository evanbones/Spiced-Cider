package com.evandev.spicedcider.datagen.providers;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModBuildingBlocks;
import com.evandev.spicedcider.registry.ModBuildingBlocks.Def;
import com.evandev.spicedcider.registry.ModBuildingBlocks.Kind;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModBuildingBlocksProvider implements DataProvider {
    private final PackOutput output;
    private final List<CompletableFuture<?>> futures = new ArrayList<>();
    private CachedOutput cache;

    public ModBuildingBlocksProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        this.cache = cache;
        this.futures.clear();
        Map<String, JsonArray> shapeMap = new LinkedHashMap<>();
        for (Def def : ModBuildingBlocks.DEFS) {
            switch (def.kind()) {
                case CUBE -> cube(def);
                case PILLAR -> pillar(def);
                case STAIRS -> stairs(def);
                case SLAB -> slab(def);
                case WALL -> wall(def);
            }
            loot(def);
            recipes(def);
            if (def.parent() != null) {
                shapeMap.computeIfAbsent(def.parent(), key -> new JsonArray()).add(def.id());
            }
        }
        JsonObject add = new JsonObject();
        shapeMap.forEach(add::add);
        JsonObject root = new JsonObject();
        root.addProperty("priority", 1000);
        root.add("add", add);
        save(data("clutternomore", "shape_map/spicedcider_building_blocks"), root);
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private Path assets(String path) {
        return output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(SpicedCider.MOD_ID).resolve(path + ".json");
    }

    private Path data(String namespace, String path) {
        return output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(namespace).resolve(path + ".json");
    }

    private void save(Path path, JsonElement json) {
        futures.add(DataProvider.saveStable(cache, json, path));
    }

    private String model(String path) {
        return SpicedCider.MOD_ID + ":block/" + path;
    }

    private JsonObject textures(String... pairs) {
        JsonObject textures = new JsonObject();
        for (int i = 0; i < pairs.length; i += 2) {
            textures.addProperty(pairs[i], pairs[i + 1]);
        }
        return textures;
    }

    private void blockModel(String path, String parent, JsonObject textures) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);
        json.add("textures", textures);
        save(assets("models/block/" + path), json);
    }

    private void itemModel(Def def, String parent) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);
        save(assets("models/item/" + def.path()), json);
    }

    private JsonObject variant(String model, int x, int y, boolean uvlock) {
        JsonObject json = new JsonObject();
        json.addProperty("model", model);
        if (x != 0) json.addProperty("x", x);
        if (y != 0) json.addProperty("y", y);
        if (uvlock) json.addProperty("uvlock", true);
        return json;
    }

    private void variants(Def def, Map<String, JsonObject> entries) {
        JsonObject variants = new JsonObject();
        entries.forEach(variants::add);
        JsonObject json = new JsonObject();
        json.add("variants", variants);
        save(assets("blockstates/" + def.path()), json);
    }

    private void cubeModel(String path, Def def) {
        if (def.side().equals(def.top()) && def.side().equals(def.bottom())) {
            blockModel(path, "minecraft:block/cube_all", textures("all", def.side()));
        } else {
            blockModel(path, "minecraft:block/cube_bottom_top", textures("side", def.side(), "top", def.top(), "bottom", def.bottom()));
        }
    }

    private void cube(Def def) {
        cubeModel(def.path(), def);
        variants(def, Map.of("", variant(model(def.path()), 0, 0, false)));
        itemModel(def, model(def.path()));
    }

    private void pillar(Def def) {
        blockModel(def.path(), "minecraft:block/cube_column", textures("end", def.top(), "side", def.side()));
        blockModel(def.path() + "_horizontal", "minecraft:block/cube_column_horizontal", textures("end", def.top(), "side", def.side()));
        Map<String, JsonObject> entries = new LinkedHashMap<>();
        entries.put("axis=x", variant(model(def.path() + "_horizontal"), 90, 90, false));
        entries.put("axis=y", variant(model(def.path()), 0, 0, false));
        entries.put("axis=z", variant(model(def.path() + "_horizontal"), 90, 0, false));
        variants(def, entries);
        itemModel(def, model(def.path()));
    }

    private void stairs(Def def) {
        JsonObject tex = textures("bottom", def.bottom(), "side", def.side(), "top", def.top());
        blockModel(def.path(), "minecraft:block/stairs", tex);
        blockModel(def.path() + "_inner", "minecraft:block/inner_stairs", tex);
        blockModel(def.path() + "_outer", "minecraft:block/outer_stairs", tex);
        Map<String, JsonObject> entries = new LinkedHashMap<>();
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            for (Half half : Half.values()) {
                for (StairsShape shape : StairsShape.values()) {
                    int y = (int) facing.getClockWise().toYRot();
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) y += 270;
                    if (shape != StairsShape.STRAIGHT && half == Half.TOP) y += 90;
                    y %= 360;
                    String suffix = switch (shape) {
                        case STRAIGHT -> "";
                        case INNER_LEFT, INNER_RIGHT -> "_inner";
                        case OUTER_LEFT, OUTER_RIGHT -> "_outer";
                    };
                    String key = "facing=" + facing.getSerializedName() + ",half=" + half.getSerializedName() + ",shape=" + shape.getSerializedName();
                    entries.put(key, variant(model(def.path() + suffix), half == Half.TOP ? 180 : 0, y, y != 0 || half == Half.TOP));
                }
            }
        }
        variants(def, entries);
        itemModel(def, model(def.path()));
    }

    private void slab(Def def) {
        JsonObject tex = textures("bottom", def.bottom(), "side", def.side(), "top", def.top());
        blockModel(def.path(), "minecraft:block/slab", tex);
        blockModel(def.path() + "_top", "minecraft:block/slab_top", tex);
        String full;
        if (def.parent() != null && def.parent().startsWith(SpicedCider.MOD_ID + ":")) {
            full = model(def.parent().substring(SpicedCider.MOD_ID.length() + 1));
        } else {
            cubeModel(def.path() + "_double", def);
            full = model(def.path() + "_double");
        }
        Map<String, JsonObject> entries = new LinkedHashMap<>();
        entries.put("type=bottom", variant(model(def.path()), 0, 0, false));
        entries.put("type=double", variant(full, 0, 0, false));
        entries.put("type=top", variant(model(def.path() + "_top"), 0, 0, false));
        variants(def, entries);
        itemModel(def, model(def.path()));
    }

    private JsonObject when(String key, String value) {
        JsonObject when = new JsonObject();
        when.addProperty(key, value);
        return when;
    }

    private JsonObject part(JsonObject when, JsonObject apply) {
        JsonObject part = new JsonObject();
        part.add("when", when);
        part.add("apply", apply);
        return part;
    }

    private void wall(Def def) {
        JsonObject tex = textures("wall", def.side());
        blockModel(def.path() + "_post", "minecraft:block/template_wall_post", tex);
        blockModel(def.path() + "_side", "minecraft:block/template_wall_side", tex);
        blockModel(def.path() + "_side_tall", "minecraft:block/template_wall_side_tall", tex);
        blockModel(def.path() + "_inventory", "minecraft:block/wall_inventory", tex);
        JsonArray multipart = new JsonArray();
        multipart.add(part(when("up", "true"), variant(model(def.path() + "_post"), 0, 0, false)));
        String[] sides = {"north", "east", "south", "west"};
        for (String height : new String[]{"low", "tall"}) {
            String suffix = height.equals("low") ? "_side" : "_side_tall";
            for (int i = 0; i < sides.length; i++) {
                multipart.add(part(when(sides[i], height), variant(model(def.path() + suffix), 0, i * 90, true)));
            }
        }
        JsonObject json = new JsonObject();
        json.add("multipart", multipart);
        save(assets("blockstates/" + def.path()), json);
        itemModel(def, model(def.path() + "_inventory"));
    }

    private void loot(Def def) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", def.id());
        JsonArray functions = new JsonArray();
        if (def.kind() == Kind.SLAB) {
            JsonObject properties = new JsonObject();
            properties.addProperty("type", "double");
            JsonObject condition = new JsonObject();
            condition.addProperty("condition", "minecraft:block_state_property");
            condition.addProperty("block", def.id());
            condition.add("properties", properties);
            JsonArray conditions = new JsonArray();
            conditions.add(condition);
            JsonObject setCount = new JsonObject();
            setCount.addProperty("function", "minecraft:set_count");
            setCount.addProperty("count", 2.0);
            setCount.addProperty("add", false);
            setCount.add("conditions", conditions);
            functions.add(setCount);
            JsonObject decay = new JsonObject();
            decay.addProperty("function", "minecraft:explosion_decay");
            functions.add(decay);
            entry.add("functions", functions);
        }
        JsonArray entries = new JsonArray();
        entries.add(entry);
        JsonObject pool = new JsonObject();
        pool.addProperty("bonus_rolls", 0.0);
        pool.add("entries", entries);
        pool.addProperty("rolls", 1.0);
        if (def.kind() != Kind.SLAB) {
            JsonObject survives = new JsonObject();
            survives.addProperty("condition", "minecraft:survives_explosion");
            JsonArray conditions = new JsonArray();
            conditions.add(survives);
            pool.add("conditions", conditions);
        }
        JsonArray pools = new JsonArray();
        pools.add(pool);
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:block");
        json.add("pools", pools);
        json.addProperty("random_sequence", SpicedCider.MOD_ID + ":blocks/" + def.path());
        save(data(SpicedCider.MOD_ID, "loot_table/blocks/" + def.path()), json);
    }

    private JsonObject item(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("item", id);
        return json;
    }

    private JsonObject result(Def def, int count) {
        JsonObject json = new JsonObject();
        json.addProperty("count", count);
        json.addProperty("id", def.id());
        return json;
    }

    private void recipe(Def def, String name, JsonObject json) {
        if (!def.requires().isEmpty()) {
            JsonArray conditions = new JsonArray();
            for (String mod : def.requires()) {
                JsonObject condition = new JsonObject();
                condition.addProperty("type", "neoforge:mod_loaded");
                condition.addProperty("modid", mod);
                conditions.add(condition);
            }
            JsonObject wrapped = new JsonObject();
            wrapped.add("neoforge:conditions", conditions);
            json.entrySet().forEach(e -> wrapped.add(e.getKey(), e.getValue()));
            json = wrapped;
        }
        save(data(SpicedCider.MOD_ID, "recipe/" + name), json);
    }

    private void shaped(Def def, String name, int count, String from, String... pattern) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");
        json.addProperty("category", "building");
        JsonObject key = new JsonObject();
        key.add("#", item(from));
        json.add("key", key);
        JsonArray rows = new JsonArray();
        for (String row : pattern) rows.add(row);
        json.add("pattern", rows);
        json.add("result", result(def, count));
        recipe(def, name, json);
    }

    private void shapeless(Def def, String name, String... inputs) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shapeless");
        json.addProperty("category", "building");
        JsonArray ingredients = new JsonArray();
        for (String input : inputs) ingredients.add(item(input));
        json.add("ingredients", ingredients);
        json.add("result", result(def, 1));
        recipe(def, name, json);
    }

    private void recipes(Def def) {
        String from = def.craftFrom();
        switch (def.craft()) {
            case SHAPE -> {
                switch (def.kind()) {
                    case STAIRS -> shaped(def, def.path(), 4, from, "#  ", "## ", "###");
                    case SLAB -> shaped(def, def.path(), 6, from, "###");
                    case WALL -> shaped(def, def.path(), 6, from, "###", "###");
                    default -> {
                    }
                }
            }
            case MOSSY -> {
                shapeless(def, def.path() + "_from_moss_block", from, "minecraft:moss_block");
                shapeless(def, def.path() + "_from_vine", from, "minecraft:vine");
            }
            case POLISH, TILES -> shaped(def, def.path(), 4, from, "##", "##");
            case PILLAR -> shaped(def, def.path(), 2, from, "#", "#");
            case NONE -> {
            }
        }
        int count = def.kind() == Kind.SLAB ? 2 : 1;
        for (String source : def.cut()) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "minecraft:stonecutting");
            json.add("ingredient", item(source));
            json.add("result", result(def, count));
            String sourcePath = source.substring(source.indexOf(':') + 1);
            recipe(def, def.path() + "_from_" + sourcePath + "_stonecutting", json);
        }
    }

    @Override
    public @NotNull String getName() {
        return "Spiced Cider building blocks";
    }
}

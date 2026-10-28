package com.evandev.spicedcider.compat.modestmeals;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ThermalEffectRemap {
    private static final String COLD = "modest_meals:cold_resistance";
    private static final String HEAT = "modest_meals:heat_resistance";
    private static final Map<String, String> INGREDIENT_EFFECTS = Map.of(
            COLD, "modest_meals:strength",
            HEAT, "modest_meals:fire_resistance");
    private static final Map<String, String> TRAIT_EFFECTS = Map.of(
            COLD, "minecraft:strength",
            HEAT, "minecraft:fire_resistance");
    private static final Map<String, String> TARGET_EFFECTS = Map.of(
            "bountifulfares:hoary_apple", "modest_meals:resistance");

    public static Map<ResourceLocation, JsonElement> ingredients(Map<ResourceLocation, JsonElement> resources) {
        return remap(resources, INGREDIENT_EFFECTS);
    }

    public static Map<ResourceLocation, JsonElement> traits(Map<ResourceLocation, JsonElement> resources) {
        return remap(resources, TRAIT_EFFECTS);
    }

    private static Map<ResourceLocation, JsonElement> remap(Map<ResourceLocation, JsonElement> resources, Map<String, String> effects) {
        Map<ResourceLocation, JsonElement> out = new LinkedHashMap<>(resources);
        for (Map.Entry<ResourceLocation, JsonElement> entry : out.entrySet()) {
            String text = entry.getValue().toString();
            if (!text.contains(COLD) && !text.contains(HEAT)) continue;
            JsonElement copy = entry.getValue().deepCopy();
            walk(copy, null, effects);
            entry.setValue(copy);
        }
        return out;
    }

    private static void walk(JsonElement element, String target, Map<String, String> effects) {
        if (element instanceof JsonArray array) {
            for (JsonElement child : array) walk(child, target, effects);
        } else if (element instanceof JsonObject object) {
            if (object.get("target") instanceof JsonPrimitive primitive && primitive.isString()) target = primitive.getAsString();
            if (object.get("effect") instanceof JsonPrimitive primitive && primitive.isString()) {
                String replacement = effects.get(primitive.getAsString());
                if (replacement != null) {
                    object.addProperty("effect", target != null && TARGET_EFFECTS.containsKey(target) ? TARGET_EFFECTS.get(target) : replacement);
                }
            }
            for (Map.Entry<String, JsonElement> child : object.entrySet()) walk(child.getValue(), target, effects);
        }
    }
}

package com.evandev.spicedcider.mixin.platform;

import com.blackgear.platform.common.v2.recipe.DynamicRecipeManager;
import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@IfModLoaded("platform")
@Mixin(value = DynamicRecipeManager.class, remap = false)
public class DynamicRecipeManagerMixin {

    @Inject(method = "generate", at = @At("TAIL"))
    private static void spicedcider$dropRemovedRecipes(HolderLookup.Provider registries, CallbackInfo ci) {
        Set<String> ids = new HashSet<>();
        List<Pattern> patterns = new ArrayList<>();
        List<JsonObject> replacements = new ArrayList<>();
        spicedcider$readRules(ids, patterns, replacements);
        boolean checkResults = ReliableRemoverCompat.isLoaded();
        DynamicRecipeManager.getRecipes().entrySet().removeIf(entry -> {
            String id = entry.getKey().toString();
            if (ids.contains(id) || patterns.stream().anyMatch(pattern -> pattern.matcher(id).matches())) return true;
            return checkResults && spicedcider$resultRemoved(entry.getValue());
        });
        for (Map.Entry<ResourceLocation, JsonElement> entry : DynamicRecipeManager.getRecipes().entrySet()) {
            if (!entry.getValue().isJsonObject()) continue;
            for (JsonObject rule : replacements) {
                if (!spicedcider$idMatches(rule.get("filter"), entry.getKey().toString())) continue;
                spicedcider$replaceInputs(entry.getValue(), spicedcider$strings(rule.get("target")), spicedcider$ingredient(rule.get("replacement")));
            }
        }
    }

    private static void spicedcider$readRules(Set<String> ids, List<Pattern> patterns, List<JsonObject> replacements) {
        Path folder = FMLPaths.CONFIGDIR.get().resolve("reliable_recipes");
        if (!Files.isDirectory(folder)) return;
        try (Stream<Path> files = Files.walk(folder)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".json")).toList()) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    JsonElement root = JsonParser.parseReader(reader);
                    if (!root.isJsonArray()) continue;
                    for (JsonElement rule : root.getAsJsonArray()) {
                        if (!rule.isJsonObject()) continue;
                        JsonObject object = rule.getAsJsonObject();
                        if (!object.has("action")) continue;
                        String action = object.get("action").getAsString();
                        if ("replace_input".equals(action) && spicedcider$isSimpleReplacement(object)) {
                            replacements.add(object);
                            continue;
                        }
                        if (!"remove_recipe".equals(action) || !object.has("id")) continue;
                        JsonElement idElement = object.get("id");
                        JsonArray values = idElement.isJsonArray() ? idElement.getAsJsonArray() : new JsonArray();
                        if (idElement.isJsonPrimitive()) values.add(idElement);
                        for (JsonElement value : values) {
                            if (!value.isJsonPrimitive()) continue;
                            String text = value.getAsString();
                            if (text.length() > 2 && text.startsWith("/") && text.endsWith("/")) {
                                patterns.add(Pattern.compile(text.substring(1, text.length() - 1)));
                            } else {
                                ids.add(text);
                            }
                        }
                    }
                } catch (IOException | RuntimeException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static boolean spicedcider$isSimpleReplacement(JsonObject rule) {
        if (spicedcider$strings(rule.get("target")).isEmpty() || spicedcider$ingredient(rule.get("replacement")) == null) return false;
        if (!rule.keySet().stream().allMatch(key -> key.equals("action") || key.equals("target") || key.equals("replacement") || key.equals("filter"))) return false;
        JsonElement filter = rule.get("filter");
        return filter == null || filter.isJsonObject() && filter.getAsJsonObject().keySet().equals(Set.of("id"));
    }

    private static boolean spicedcider$idMatches(JsonElement filter, String id) {
        if (filter == null) return true;
        for (String value : spicedcider$strings(filter.getAsJsonObject().get("id"))) {
            if (value.length() > 2 && value.startsWith("/") && value.endsWith("/")) {
                if (Pattern.compile(value.substring(1, value.length() - 1)).matcher(id).matches()) return true;
            } else if (value.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> spicedcider$strings(JsonElement element) {
        List<String> values = new ArrayList<>();
        if (element == null) return values;
        if (element.isJsonPrimitive()) {
            values.add(element.getAsString());
        } else if (element.isJsonArray()) {
            for (JsonElement value : element.getAsJsonArray()) {
                if (value.isJsonPrimitive()) values.add(value.getAsString());
            }
        }
        return values;
    }

    private static JsonElement spicedcider$ingredient(JsonElement replacement) {
        if (replacement == null) return null;
        if (replacement.isJsonPrimitive()) {
            String value = replacement.getAsString();
            JsonObject ingredient = new JsonObject();
            if (value.startsWith("#")) {
                ingredient.addProperty("tag", value.substring(1));
            } else {
                ingredient.addProperty("item", value);
            }
            return ingredient;
        }
        if (!replacement.isJsonArray()) return null;
        JsonArray ingredients = new JsonArray();
        for (JsonElement value : replacement.getAsJsonArray()) {
            JsonElement ingredient = spicedcider$ingredient(value);
            if (ingredient == null || ingredient.isJsonArray()) return null;
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    private static boolean spicedcider$isTarget(JsonElement element, List<String> targets) {
        if (!(element instanceof JsonObject object) || object.size() != 1) return false;
        if (object.get("item") instanceof JsonElement item && item.isJsonPrimitive()) return targets.contains(item.getAsString());
        if (object.get("tag") instanceof JsonElement tag && tag.isJsonPrimitive()) return targets.contains("#" + tag.getAsString());
        return false;
    }

    private static void spicedcider$replaceInputs(JsonElement element, List<String> targets, JsonElement replacement) {
        if (element instanceof JsonObject object) {
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if (entry.getKey().equals("result")) continue;
                if (spicedcider$isTarget(entry.getValue(), targets)) {
                    entry.setValue(replacement.deepCopy());
                } else {
                    spicedcider$replaceInputs(entry.getValue(), targets, replacement);
                }
            }
        } else if (element instanceof JsonArray array) {
            for (int i = 0; i < array.size(); i++) {
                if (spicedcider$isTarget(array.get(i), targets)) {
                    array.set(i, replacement.deepCopy());
                } else {
                    spicedcider$replaceInputs(array.get(i), targets, replacement);
                }
            }
        }
    }

    private static boolean spicedcider$resultRemoved(JsonElement recipe) {
        if (!recipe.isJsonObject() || !(recipe.getAsJsonObject().get("result") instanceof JsonObject result)) return false;
        JsonElement id = result.has("id") ? result.get("id") : result.get("item");
        if (id == null || !id.isJsonPrimitive()) return false;
        ResourceLocation location = ResourceLocation.tryParse(id.getAsString());
        if (location == null || !BuiltInRegistries.ITEM.containsKey(location)) return false;
        return ReliableRemoverCompat.isRemoved(BuiltInRegistries.ITEM.get(location));
    }
}

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
        spicedcider$readRemovedIds(ids, patterns);
        boolean checkResults = ReliableRemoverCompat.isLoaded();
        DynamicRecipeManager.getRecipes().entrySet().removeIf(entry -> {
            String id = entry.getKey().toString();
            if (ids.contains(id) || patterns.stream().anyMatch(pattern -> pattern.matcher(id).matches())) return true;
            return checkResults && spicedcider$resultRemoved(entry.getValue());
        });
    }

    private static void spicedcider$readRemovedIds(Set<String> ids, List<Pattern> patterns) {
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
                        if (!object.has("action") || !"remove_recipe".equals(object.get("action").getAsString()) || !object.has("id")) continue;
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

    private static boolean spicedcider$resultRemoved(JsonElement recipe) {
        if (!recipe.isJsonObject() || !(recipe.getAsJsonObject().get("result") instanceof JsonObject result)) return false;
        JsonElement id = result.has("id") ? result.get("id") : result.get("item");
        if (id == null || !id.isJsonPrimitive()) return false;
        ResourceLocation location = ResourceLocation.tryParse(id.getAsString());
        if (location == null || !BuiltInRegistries.ITEM.containsKey(location)) return false;
        return ReliableRemoverCompat.isRemoved(BuiltInRegistries.ITEM.get(location));
    }
}

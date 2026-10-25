package com.evandev.spicedcider.compat.reliableremover;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ModRemovalFiles {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static boolean add(String itemId) throws IOException {
        Path file = fileFor(itemId);
        JsonPrimitive entry = new JsonPrimitive(itemId);

        if (!Files.exists(file)) {
            JsonArray root = new JsonArray();
            root.add(newRule(entry));
            write(file, GSON.toJson(root) + "\n");
            return true;
        }

        String text = Files.readString(file, StandardCharsets.UTF_8);
        JsonElement root = JsonParser.parseString(text);
        JsonArray target = null;
        for (JsonObject rule : rules(root)) {
            if (!isSimple(rule)) continue;
            JsonArray items = rule.getAsJsonArray("items");
            if (items.contains(entry)) return false;
            if (target == null) target = items;
        }

        if (target != null && !target.isEmpty()) {
            String last = GSON.toJson(target.get(target.size() - 1));
            int at = text.indexOf(last);
            if (at >= 0 && at == text.lastIndexOf(last)) {
                int end = at + last.length();
                String indent = text.substring(text.lastIndexOf('\n', at) + 1, at);
                String insert = indent.isBlank() ? ",\n" + indent + GSON.toJson(entry) : ", " + GSON.toJson(entry);
                String edited = text.substring(0, end) + insert + text.substring(end);
                if (parses(edited)) {
                    write(file, edited);
                    return true;
                }
            }
        }

        if (target != null) {
            target.add(entry);
        } else if (root.isJsonArray()) {
            root.getAsJsonArray().add(newRule(entry));
        } else {
            JsonArray wrapped = new JsonArray();
            wrapped.add(root);
            wrapped.add(newRule(entry));
            root = wrapped;
        }
        write(file, GSON.toJson(root) + "\n");
        return true;
    }

    public static boolean remove(String itemId) throws IOException {
        Path file = fileFor(itemId);
        if (!Files.exists(file)) return false;

        JsonPrimitive entry = new JsonPrimitive(itemId);
        String text = Files.readString(file, StandardCharsets.UTF_8);
        JsonElement root = JsonParser.parseString(text);

        int found = 0;
        for (JsonObject rule : rules(root)) {
            if (!isSimple(rule)) continue;
            JsonArray items = rule.getAsJsonArray("items");
            while (items.remove(entry)) found++;
        }
        if (found == 0) return false;

        if (rules(root).stream().allMatch(rule -> isSimple(rule) && rule.getAsJsonArray("items").isEmpty())) {
            Files.delete(file);
            return true;
        }

        String quoted = GSON.toJson(entry);
        int at = text.indexOf(quoted);
        if (found == 1 && at >= 0 && at == text.lastIndexOf(quoted)) {
            String edited = cut(text, at, at + quoted.length());
            if (parses(edited)) {
                write(file, edited);
                return true;
            }
        }

        write(file, GSON.toJson(root) + "\n");
        return true;
    }

    private static String cut(String text, int start, int end) {
        int lineStart = text.lastIndexOf('\n', start) + 1;
        int lineEnd = text.indexOf('\n', end);
        if (lineEnd < 0) lineEnd = text.length();
        String rest = text.substring(end, lineEnd).trim();

        if (text.substring(lineStart, start).isBlank() && (rest.isEmpty() || rest.equals(","))) {
            String edited = text.substring(0, lineStart) + text.substring(Math.min(lineEnd + 1, text.length()));
            if (rest.isEmpty()) {
                int comma = previousNonBlank(edited, lineStart - 1);
                if (comma >= 0 && edited.charAt(comma) == ',') {
                    edited = edited.substring(0, comma) + edited.substring(comma + 1);
                }
            }
            return edited;
        }

        int next = nextNonBlank(text, end);
        if (next < text.length() && text.charAt(next) == ',') {
            return text.substring(0, start) + text.substring(nextNonBlank(text, next + 1));
        }
        int previous = previousNonBlank(text, start - 1);
        if (previous >= 0 && text.charAt(previous) == ',') {
            return text.substring(0, previous) + text.substring(end);
        }
        return text.substring(0, start) + text.substring(end);
    }

    private static int nextNonBlank(String text, int from) {
        while (from < text.length() && Character.isWhitespace(text.charAt(from))) from++;
        return from;
    }

    private static int previousNonBlank(String text, int from) {
        while (from >= 0 && Character.isWhitespace(text.charAt(from))) from--;
        return from;
    }

    private static Path fileFor(String itemId) {
        int colon = itemId.indexOf(':');
        String namespace = colon < 0 ? "minecraft" : itemId.substring(0, colon);
        return FMLPaths.CONFIGDIR.get().resolve("reliable_remover").resolve("mods").resolve(namespace + ".json");
    }

    private static List<JsonObject> rules(JsonElement root) {
        List<JsonObject> rules = new ArrayList<>();
        if (root.isJsonObject()) {
            rules.add(root.getAsJsonObject());
        } else if (root.isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray()) {
                if (element.isJsonObject()) rules.add(element.getAsJsonObject());
            }
        }
        return rules;
    }

    private static boolean isSimple(JsonObject rule) {
        for (String key : rule.keySet()) {
            if (!key.equals("action") && !key.equals("items")) return false;
        }
        JsonElement action = rule.get("action");
        if (action != null && !(action.isJsonPrimitive() && action.getAsString().equals("remove"))) return false;
        return rule.has("items") && rule.get("items").isJsonArray();
    }

    private static JsonObject newRule(JsonPrimitive entry) {
        JsonArray items = new JsonArray();
        items.add(entry);
        JsonObject rule = new JsonObject();
        rule.addProperty("action", "remove");
        rule.add("items", items);
        return rule;
    }

    private static boolean parses(String text) {
        try {
            JsonParser.parseString(text);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static void write(Path file, String text) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, text, StandardCharsets.UTF_8);
    }
}

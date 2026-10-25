package com.evandev.spicedcider.client.options;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.settings.KeyModifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public final class PackDefaults {

    private static final Map<OptionInstance<?>, String> NAMES = new IdentityHashMap<>();
    private static final Map<OptionInstance<?>, Optional<?>> PARSED = new IdentityHashMap<>();
    private static Map<String, String> values;

    public static void record(String name, OptionInstance<?> option) {
        NAMES.putIfAbsent(option, name);
    }

    private static Map<String, String> values() {
        if (values == null) {
            values = new HashMap<>();
            Path file = FMLPaths.CONFIGDIR.get().resolve("yosbr").resolve("options.txt");
            if (Files.isRegularFile(file)) {
                try {
                    for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                        int split = line.indexOf(':');
                        if (split > 0) values.put(line.substring(0, split), line.substring(split + 1));
                    }
                } catch (IOException ignored) {
                }
            }
        }
        return values;
    }

    public static Optional<String> raw(String key) {
        return Optional.ofNullable(values().get(key));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> value(OptionInstance<T> option) {
        return (Optional<T>) PARSED.computeIfAbsent(option, PackDefaults::parse);
    }

    private static <T> Optional<T> parse(OptionInstance<T> option) {
        String name = NAMES.get(option);
        if (name == null) return Optional.empty();
        return raw(name).flatMap(raw -> {
            try {
                return option.codec().parse(JsonOps.INSTANCE, JsonParser.parseString(raw)).result();
            } catch (RuntimeException e) {
                return Optional.empty();
            }
        });
    }

    private record KeyDefault(KeyModifier modifier, InputConstants.Key key) {
    }

    private static Optional<KeyDefault> keyDefault(KeyMapping mapping) {
        return raw("key_" + mapping.getName()).map(raw -> {
            int split = raw.indexOf(':');
            if (split != -1) {
                return new KeyDefault(KeyModifier.valueFromString(raw.substring(split + 1)), InputConstants.getKey(raw.substring(0, split)));
            }
            return new KeyDefault(KeyModifier.NONE, InputConstants.getKey(raw));
        });
    }

    public static boolean isKeyDefault(KeyMapping mapping) {
        return keyDefault(mapping)
                .map(value -> value.key().equals(mapping.getKey()) && value.modifier() == mapping.getKeyModifier())
                .orElseGet(mapping::isDefault);
    }

    public static void resetKey(KeyMapping mapping) {
        Optional<KeyDefault> value = keyDefault(mapping);
        if (value.isPresent()) {
            mapping.setKeyModifierAndCode(value.get().modifier(), value.get().key());
            Minecraft.getInstance().options.setKey(mapping, value.get().key());
        } else {
            mapping.setToDefault();
            Minecraft.getInstance().options.setKey(mapping, mapping.getDefaultKey());
        }
    }
}

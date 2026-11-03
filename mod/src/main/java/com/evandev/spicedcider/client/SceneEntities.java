package com.evandev.spicedcider.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SceneEntities {
    private static final Map<String, Entity> ENTITIES = new HashMap<>();
    private static ClientLevel level;

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Entity> T get(String key, EntityType<T> type, Consumer<T> setup) {
        ClientLevel current = Minecraft.getInstance().level;
        if (current == null) return null;
        if (current != level) {
            ENTITIES.clear();
            level = current;
        }
        return (T) ENTITIES.computeIfAbsent(key, k -> {
            T entity = type.create(current);
            if (entity != null) setup.accept(entity);
            return entity;
        });
    }

    public static void clear() {
        ENTITIES.clear();
        level = null;
    }
}

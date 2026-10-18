package com.evandev.spicedcider.compat.mixedlitter;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.tazer.mixed_litter.actions.VariantActionType;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ResolvedActionCache {
    private static final boolean NO_MANS_LAND = ModList.get().isLoaded("nomansland");
    private static final Map<Key, Hit> CACHE = new HashMap<>();

    public static synchronized VariantActionType resolve(VariantActionType type, JsonObject actionArgs, JsonObject variantArgs,
                                                         JsonObject defaults, Operation<VariantActionType> original) {
        Key key = new Key(type, actionArgs, variantArgs);
        Hit hit = CACHE.get(key);
        if (hit != null && Objects.equals(hit.defaults, defaults)) return hit.result;
        VariantActionType result = original.call(type, actionArgs, variantArgs, defaults);
        VariantActionType stored = result != type ? result : NO_MANS_LAND ? NoMansLandActionCopies.copy(result) : null;
        if (stored != null) CACHE.put(key, new Hit(defaults, stored));
        return result;
    }

    public static synchronized void clear() {
        CACHE.clear();
    }

    private record Hit(JsonObject defaults, VariantActionType result) {
    }

    private record Key(Object type, Object actionArgs, Object variantArgs) {
        @Override
        public boolean equals(Object o) {
            return o instanceof Key k && k.type == type && k.actionArgs == actionArgs && k.variantArgs == variantArgs;
        }

        @Override
        public int hashCode() {
            return (System.identityHashCode(type) * 31 + System.identityHashCode(actionArgs)) * 31 + System.identityHashCode(variantArgs);
        }
    }
}

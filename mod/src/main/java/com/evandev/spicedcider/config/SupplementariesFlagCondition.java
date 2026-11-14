package com.evandev.spicedcider.config;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public record SupplementariesFlagCondition(String flag) implements ICondition {
    public static final MapCodec<SupplementariesFlagCondition> CODEC = Codec.STRING.fieldOf("flag")
            .xmap(SupplementariesFlagCondition::new, SupplementariesFlagCondition::flag);

    private static final ResourceLocation FLAG = ResourceLocation.fromNamespaceAndPath("supplementaries", "flag");

    @Override
    public boolean test(@NotNull IContext context) {
        if (!ModList.get().isLoaded("supplementaries")) return false;
        MapCodec<? extends ICondition> codec = NeoForgeRegistries.CONDITION_SERIALIZERS.get(FLAG);
        if (codec == null) return false;
        JsonObject json = new JsonObject();
        json.addProperty("flag", flag);
        return codec.codec().parse(JsonOps.INSTANCE, json).result()
                .map(condition -> condition.test(context))
                .orElse(false);
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}

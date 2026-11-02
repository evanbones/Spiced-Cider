package com.evandev.spicedcider.datagen.providers;

import com.google.common.hash.Hashing;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider implements DataProvider {
    private static final Map<String, String> REQUIRED_MODS = Map.of(
            "polished_glowstone_stairs", "infernalexp",
            "polished_glowstone_slab", "infernalexp",
            "polished_glowstone_wall", "infernalexp",
            "twisted_sign", "architects_palette",
            "twisted_hanging_sign", "architects_palette"
    );

    private final LootTableProvider delegate;

    private ModLootTableProvider(LootTableProvider delegate) {
        this.delegate = delegate;
    }

    public static DataProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        return new ModLootTableProvider(new LootTableProvider(
                output,
                Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(ModBlockLootProvider::new, LootContextParamSets.BLOCK)
                ),
                lookupProvider
        ));
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        return delegate.run((path, bytes, hash) -> {
            String mod = REQUIRED_MODS.get(tableName(path));
            if (mod == null) {
                output.writeIfNeeded(path, bytes, hash);
                return;
            }
            String json = new String(bytes, StandardCharsets.UTF_8);
            String conditions = "{\n  \"neoforge:conditions\": [\n    {\n      \"type\": \"neoforge:mod_loaded\",\n      \"modid\": \"" + mod + "\"\n    }\n  ],";
            byte[] conditioned = json.replaceFirst("^\\{", conditions.replace("\\", "\\\\").replace("$", "\\$")).getBytes(StandardCharsets.UTF_8);
            output.writeIfNeeded(path, conditioned, Hashing.sha1().hashBytes(conditioned));
        });
    }

    private static String tableName(Path path) {
        String file = path.getFileName().toString();
        return path.getParent() != null && path.getParent().getFileName().toString().equals("blocks") && file.endsWith(".json")
                ? file.substring(0, file.length() - 5) : "";
    }

    @Override
    public @NotNull String getName() {
        return delegate.getName();
    }
}

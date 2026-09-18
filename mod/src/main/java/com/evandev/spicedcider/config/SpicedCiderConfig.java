package com.evandev.spicedcider.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SpicedCiderConfig {
    private static final String LANG_PATH = "assets/spicedcider/lang/en_us.json";
    private static final JsonObject LANG = loadLang();

    public static final ModConfigSpec STARTUP_SPEC;
    public static final Startup STARTUP;

    public static final ModConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    public static final ModConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Startup, ModConfigSpec> startupPair = new ModConfigSpec.Builder().configure(Startup::new);
        STARTUP_SPEC = startupPair.getRight();
        STARTUP = startupPair.getLeft();

        final Pair<Common, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();

        final Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();
    }

    public static <T> T clientOr(ModConfigSpec.ConfigValue<T> value, T fallback) {
        return CLIENT_SPEC.isLoaded() ? value.get() : fallback;
    }

    private static JsonObject loadLang() {
        try (InputStream stream = SpicedCiderConfig.class.getClassLoader().getResourceAsStream(LANG_PATH)) {
            if (stream == null) {
                throw new IllegalStateException("Missing " + LANG_PATH);
            }
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + LANG_PATH, e);
        }
    }

    private static String tooltip(String key) {
        String fullKey = "option.spicedcider." + key + ".tooltip";
        if (!LANG.has(fullKey)) {
            throw new IllegalStateException("Missing lang key " + fullKey);
        }
        return LANG.get(fullKey).getAsString();
    }

    public static class Startup {
        public final ModConfigSpec.BooleanValue blockBoxWoodVariants;
        public final ModConfigSpec.BooleanValue skeletonHealthNerf;
        public final ModConfigSpec.BooleanValue unifiedPetArmor;

        public Startup(ModConfigSpec.Builder builder) {
            blockBoxWoodVariants = builder
                    .comment(tooltip("blockBoxWoodVariants"))
                    .translation("option.spicedcider.blockBoxWoodVariants")
                    .define("blockBoxWoodVariants", true);

            skeletonHealthNerf = builder
                    .comment(tooltip("skeletonHealthNerf"))
                    .translation("option.spicedcider.skeletonHealthNerf")
                    .define("skeletonHealthNerf", false);

            unifiedPetArmor = builder
                    .comment(tooltip("unifiedPetArmor"))
                    .translation("option.spicedcider.unifiedPetArmor")
                    .define("unifiedPetArmor", false);

        }
    }

    public static class Common {
        public final ModConfigSpec.BooleanValue oldWisteriaTrees;
        public final ModConfigSpec.BooleanValue slimeTimeDisableItemMerging;

        public final ModConfigSpec.BooleanValue bedExplosionPrevention;
        public final ModConfigSpec.BooleanValue respawnAnchorExplosionPrevention;
        public final ModConfigSpec.BooleanValue endCrystalPlaceAnywhere;
        public final ModConfigSpec.BooleanValue endCrystalHealing;
        public final ModConfigSpec.BooleanValue keepBrokenItems;
        public final ModConfigSpec.BooleanValue wolvesWearAnyArmor;
        public final ModConfigSpec.BooleanValue spiderRangedWebAttacks;
        public final ModConfigSpec.BooleanValue removeQuasiConnectivity;

        public final ModConfigSpec.BooleanValue disableRecipeBookTracking;
        public final ModConfigSpec.BooleanValue skipRedundantBlockCacheRebuild;

        public Common(ModConfigSpec.Builder builder) {
            builder.push("compat");

            oldWisteriaTrees = builder
                    .comment(tooltip("oldWisteriaTrees"))
                    .translation("option.spicedcider.oldWisteriaTrees")
                    .define("oldWisteriaTrees", true);

            slimeTimeDisableItemMerging = builder
                    .comment(tooltip("slimeTimeDisableItemMerging"))
                    .translation("option.spicedcider.slimeTimeDisableItemMerging")
                    .define("slimeTimeDisableItemMerging", true);

            builder.pop();
            builder.push("gameplay");

            bedExplosionPrevention = builder
                    .comment(tooltip("bedExplosionPrevention"))
                    .translation("option.spicedcider.bedExplosionPrevention")
                    .define("bedExplosionPrevention", false);

            respawnAnchorExplosionPrevention = builder
                    .comment(tooltip("respawnAnchorExplosionPrevention"))
                    .translation("option.spicedcider.respawnAnchorExplosionPrevention")
                    .define("respawnAnchorExplosionPrevention", false);

            endCrystalPlaceAnywhere = builder
                    .comment(tooltip("endCrystalPlaceAnywhere"))
                    .translation("option.spicedcider.endCrystalPlaceAnywhere")
                    .define("endCrystalPlaceAnywhere", false);

            endCrystalHealing = builder
                    .comment(tooltip("endCrystalHealing"))
                    .translation("option.spicedcider.endCrystalHealing")
                    .define("endCrystalHealing", false);

            keepBrokenItems = builder
                    .comment(tooltip("keepBrokenItems"))
                    .translation("option.spicedcider.keepBrokenItems")
                    .define("keepBrokenItems", false);

            wolvesWearAnyArmor = builder
                    .comment(tooltip("wolvesWearAnyArmor"))
                    .translation("option.spicedcider.wolvesWearAnyArmor")
                    .define("wolvesWearAnyArmor", false);

            spiderRangedWebAttacks = builder
                    .comment(tooltip("spiderRangedWebAttacks"))
                    .translation("option.spicedcider.spiderRangedWebAttacks")
                    .define("spiderRangedWebAttacks", false);

            removeQuasiConnectivity = builder
                    .comment(tooltip("removeQuasiConnectivity"))
                    .translation("option.spicedcider.removeQuasiConnectivity")
                    .define("removeQuasiConnectivity", false);

            builder.pop();
            builder.push("performance");

            disableRecipeBookTracking = builder
                    .comment(tooltip("disableRecipeBookTracking"))
                    .translation("option.spicedcider.disableRecipeBookTracking")
                    .define("disableRecipeBookTracking", true);

            skipRedundantBlockCacheRebuild = builder
                    .comment(tooltip("skipRedundantBlockCacheRebuild"))
                    .translation("option.spicedcider.skipRedundantBlockCacheRebuild")
                    .define("skipRedundantBlockCacheRebuild", true);

            builder.pop();
        }
    }

    public static class Client {
        public final ModConfigSpec.BooleanValue randomWorldNaming;

        public final ModConfigSpec.BooleanValue customDeathSound;
        public final ModConfigSpec.BooleanValue unmineableBlockSparks;

        public final ModConfigSpec.BooleanValue customWindowTitle;
        public final ModConfigSpec.ConfigValue<String> windowTitleFormat;
        public final ModConfigSpec.BooleanValue oldProgressScreen;

        public final ModConfigSpec.BooleanValue vistaMirrorPerfFixes;
        public final ModConfigSpec.IntValue vistaMirrorReflectionDistance;
        public final ModConfigSpec.DoubleValue vistaMirrorUpdateFps;
        public final ModConfigSpec.DoubleValue vistaMirrorMinUpdateFps;
        public final ModConfigSpec.DoubleValue vistaMirrorIdleUpdateFps;
        public final ModConfigSpec.DoubleValue vistaMirrorThrottleBudgetMs;

        public final ModConfigSpec.BooleanValue sodiumLightingParityFix;

        public Client(ModConfigSpec.Builder builder) {
            builder.push("compat");

            sodiumLightingParityFix = builder
                    .comment(tooltip("sodiumLightingParityFix"))
                    .translation("option.spicedcider.sodiumLightingParityFix")
                    .define("sodiumLightingParityFix", true);

            builder.pop();
            builder.push("naming");

            randomWorldNaming = builder
                    .comment(tooltip("randomWorldNaming"))
                    .translation("option.spicedcider.randomWorldNaming")
                    .define("randomWorldNaming", true);

            builder.pop();
            builder.push("misc");

            unmineableBlockSparks = builder
                    .comment(tooltip("unmineableBlockSparks"))
                    .translation("option.spicedcider.unmineableBlockSparks")
                    .define("unmineableBlockSparks", true);

            customDeathSound = builder
                    .comment(tooltip("customDeathSound"))
                    .translation("option.spicedcider.customDeathSound")
                    .define("customDeathSound", true);

            customWindowTitle = builder
                    .comment(tooltip("customWindowTitle"))
                    .translation("option.spicedcider.customWindowTitle")
                    .define("customWindowTitle", false);

            windowTitleFormat = builder
                    .comment(tooltip("windowTitleFormat"))
                    .translation("option.spicedcider.windowTitleFormat")
                    .define("windowTitleFormat", "Minecraft - Spiced Cider %v");

            oldProgressScreen = builder
                    .comment(tooltip("oldProgressScreen"))
                    .translation("option.spicedcider.oldProgressScreen")
                    .define("oldProgressScreen", true);

            builder.pop();
            builder.push("vista");

            vistaMirrorPerfFixes = builder
                    .comment(tooltip("vistaMirrorPerfFixes"))
                    .translation("option.spicedcider.vistaMirrorPerfFixes")
                    .define("vistaMirrorPerfFixes", true);

            vistaMirrorReflectionDistance = builder
                    .comment(tooltip("vistaMirrorReflectionDistance"))
                    .translation("option.spicedcider.vistaMirrorReflectionDistance")
                    .defineInRange("vistaMirrorReflectionDistance", 64, 16, 2048);

            vistaMirrorUpdateFps = builder
                    .comment(tooltip("vistaMirrorUpdateFps"))
                    .translation("option.spicedcider.vistaMirrorUpdateFps")
                    .defineInRange("vistaMirrorUpdateFps", 15.0, 1.0, 240.0);

            vistaMirrorMinUpdateFps = builder
                    .comment(tooltip("vistaMirrorMinUpdateFps"))
                    .translation("option.spicedcider.vistaMirrorMinUpdateFps")
                    .defineInRange("vistaMirrorMinUpdateFps", 4.0, 0.25, 240.0);

            vistaMirrorIdleUpdateFps = builder
                    .comment(tooltip("vistaMirrorIdleUpdateFps"))
                    .translation("option.spicedcider.vistaMirrorIdleUpdateFps")
                    .defineInRange("vistaMirrorIdleUpdateFps", 5.0, 0.25, 240.0);

            vistaMirrorThrottleBudgetMs = builder
                    .comment(tooltip("vistaMirrorThrottleBudgetMs"))
                    .translation("option.spicedcider.vistaMirrorThrottleBudgetMs")
                    .defineInRange("vistaMirrorThrottleBudgetMs", 3.5, 0.5, 1000.0);

            builder.pop();
        }
    }
}

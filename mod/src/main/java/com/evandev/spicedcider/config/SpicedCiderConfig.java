package com.evandev.spicedcider.config;

import com.evandev.spicedcider.content.item.CaveMapHeights;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public static <T> T startupOr(ModConfigSpec.ConfigValue<T> value, T fallback) {
        return STARTUP_SPEC.isLoaded() ? value.get() : fallback;
    }

    public static <T> T commonOr(ModConfigSpec.ConfigValue<T> value, T fallback) {
        return COMMON_SPEC.isLoaded() ? value.get() : fallback;
    }

    public static boolean feature(String name) {
        ModConfigSpec.BooleanValue value = COMMON.features.get(name);
        if (value == null) throw new IllegalArgumentException("Unknown feature " + name);
        return commonOr(value, true);
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
        public final ModConfigSpec.BooleanValue leavesNotSuffocating;
        public final ModConfigSpec.BooleanValue torchflowerLight;
        public final ModConfigSpec.BooleanValue drinkIntoxication;
        public final ModConfigSpec.BooleanValue replaceFarmersDelightComfort;
        public final ModConfigSpec.BooleanValue tideSpawnEggs;
        public final ModConfigSpec.BooleanValue sulfurCavesRework;
        public final ModConfigSpec.BooleanValue brightCageLanterns;
        public final ModConfigSpec.BooleanValue customWindowIcon;

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

            leavesNotSuffocating = builder
                    .comment(tooltip("leavesNotSuffocating"))
                    .translation("option.spicedcider.leavesNotSuffocating")
                    .define("leavesNotSuffocating", true);

            torchflowerLight = builder
                    .comment(tooltip("torchflowerLight"))
                    .translation("option.spicedcider.torchflowerLight")
                    .define("torchflowerLight", true);

            drinkIntoxication = builder
                    .comment(tooltip("drinkIntoxication"))
                    .translation("option.spicedcider.drinkIntoxication")
                    .define("drinkIntoxication", false);

            replaceFarmersDelightComfort = builder
                    .comment(tooltip("replaceFarmersDelightComfort"))
                    .translation("option.spicedcider.replaceFarmersDelightComfort")
                    .define("replaceFarmersDelightComfort", false);

            tideSpawnEggs = builder
                    .comment(tooltip("tideSpawnEggs"))
                    .translation("option.spicedcider.tideSpawnEggs")
                    .define("tideSpawnEggs", false);

            sulfurCavesRework = builder
                    .comment(tooltip("sulfurCavesRework"))
                    .translation("option.spicedcider.sulfurCavesRework")
                    .define("sulfurCavesRework", false);

            brightCageLanterns = builder
                    .comment(tooltip("brightCageLanterns"))
                    .translation("option.spicedcider.brightCageLanterns")
                    .define("brightCageLanterns", false);

            customWindowIcon = builder
                    .comment(tooltip("customWindowIcon"))
                    .translation("option.spicedcider.customWindowIcon")
                    .define("customWindowIcon", false);
        }
    }

    public static class Common {
        public static final List<String> FEATURES = List.of(
                "renameRecipe",
                "waterloggableRedstone",
                "cloudCover",
                "monumentChests",
                "ruinedPages",
                "balloons",
                "pocketJukebox",
                "grapplingHooks",
                "strawGolems",
                "sputters",
                "sporals",
                "leafSticks",
                "placedSticks",
                "oreStoneDrops",
                "autoSmelting",
                "fireStriker",
                "deposits",
                "wormyDirt",
                "richGravel",
                "springPools",
                "deadTrees",
                "gravestones",
                "cacaoTrees",
                "pearTrees",
                "oceanDepths",
                "buildingBlocks",
                "paleGardenPlants",
                "snifferGoldenDandelions",
                "stuckArrows",
                "bottleCraftingRemainder",
                "persistentWildCats");

        public static final Set<String> FEATURES_OFF_BY_DEFAULT = Set.of("oreStoneDrops", "sporals", "autoSmelting", "stuckArrows", "bottleCraftingRemainder", "persistentWildCats");

        public final ModConfigSpec.BooleanValue oldWisteriaTrees;
        public final ModConfigSpec.BooleanValue slimeTimeDisableItemMerging;
        public final ModConfigSpec.BooleanValue easyAnvilsFreeRepairs;
        public final ModConfigSpec.BooleanValue toolRepairMaterialCounts;
        public final ModConfigSpec.BooleanValue revealThreateningMonsters;
        public final ModConfigSpec.BooleanValue fireRingNoPotions;
        public final ModConfigSpec.BooleanValue witchStewBasin;
        public final ModConfigSpec.BooleanValue copperGolemBuildingDisabled;
        public final ModConfigSpec.BooleanValue whereWindsBlowNoBonemealGrass;
        public final ModConfigSpec.BooleanValue backpackInventoryRows;
        public final ModConfigSpec.BooleanValue backpackAcceptAnything;
        public final ModConfigSpec.BooleanValue incrementalMiningVanillaSpeed;
        public final ModConfigSpec.BooleanValue dustBunnySpawners;
        public final ModConfigSpec.BooleanValue tapRedstone;
        public final ModConfigSpec.BooleanValue tapFaucetFluids;
        public final ModConfigSpec.BooleanValue pineconesOnTrees;
        public final ModConfigSpec.BooleanValue copperGolemNoChest;
        public final ModConfigSpec.BooleanValue jackOStrawRotation;
        public final ModConfigSpec.BooleanValue treeChoppingStamina;
        public final ModConfigSpec.BooleanValue ledgeClimbRework;
        public final ModConfigSpec.BooleanValue ledgeClimbStamina;
        public final ModConfigSpec.BooleanValue sittingStaminaRegen;
        public final ModConfigSpec.BooleanValue noFoodStamina;
        public final ModConfigSpec.BooleanValue mealIngredientsKeepHealth;
        public final ModConfigSpec.IntValue nourishmentDurationPercent;
        public final ModConfigSpec.BooleanValue quarkStoolSeats;
        public final ModConfigSpec.BooleanValue basinCampfireHeat;
        public final ModConfigSpec.BooleanValue calmerHerdPanic;
        public final ModConfigSpec.BooleanValue sneakUpOnRabbits;
        public final ModConfigSpec.BooleanValue weakerLeapers;
        public final ModConfigSpec.BooleanValue chalkSymbolsAboveHotbar;
        public final ModConfigSpec.BooleanValue vanillaBackportLightTweaks;
        public final ModConfigSpec.BooleanValue creakingBuff;
        public final ModConfigSpec.BooleanValue goldenDandelionTempting;
        public final ModConfigSpec.BooleanValue oceanRuinPrismarineChests;
        public final ModConfigSpec.BooleanValue wind;
        public final ModConfigSpec.BooleanValue windCreateWindmills;
        public final ModConfigSpec.BooleanValue windVaneSignal;
        public final ModConfigSpec.BooleanValue windGliders;

        public final ModConfigSpec.BooleanValue bedExplosionPrevention;
        public final ModConfigSpec.BooleanValue respawnAnchorExplosionPrevention;
        public final ModConfigSpec.BooleanValue endCrystalPlaceAnywhere;
        public final ModConfigSpec.BooleanValue endCrystalHealing;
        public final ModConfigSpec.BooleanValue keepBrokenItems;
        public final ModConfigSpec.BooleanValue brokenToolsOnCuttingBoards;
        public final ModConfigSpec.BooleanValue wolvesWearAnyArmor;
        public final ModConfigSpec.BooleanValue spiderRangedWebAttacks;
        public final ModConfigSpec.BooleanValue removeQuasiConnectivity;
        public final ModConfigSpec.BooleanValue harmlessGhastFireballs;
        public final ModConfigSpec.BooleanValue logsNeedAxe;
        public final ModConfigSpec.BooleanValue stepUp;
        public final ModConfigSpec.BooleanValue hammersCannotMine;
        public final ModConfigSpec.BooleanValue treeBark;
        public final ModConfigSpec.BooleanValue mossNeedsHoe;
        public final ModConfigSpec.BooleanValue blockDyeing;
        public final ModConfigSpec.BooleanValue composterWorms;
        public final ModConfigSpec.BooleanValue azaleaTreePrevention;
        public final ModConfigSpec.BooleanValue noLeafSpawns;
        public final ModConfigSpec.BooleanValue zombieTorches;
        public final ModConfigSpec.BooleanValue darkMushrooms;
        public final ModConfigSpec.BooleanValue piglinWeaponSwap;
        public final ModConfigSpec.BooleanValue soulCampfireHaunting;
        public final ModConfigSpec.BooleanValue throwablePotionCooldown;
        public final ModConfigSpec.BooleanValue privateAdvancementMessages;
        public final ModConfigSpec.BooleanValue accurateSignHitboxes;
        public final ModConfigSpec.IntValue bundleCapacity;
        public final ModConfigSpec.IntValue fallDamageReduction;
        public final ModConfigSpec.BooleanValue noNestedBundles;
        public final ModConfigSpec.BooleanValue rangedSkeletons;
        public final ModConfigSpec.BooleanValue singleBlazeFireball;
        public final ModConfigSpec.BooleanValue noSkeletonTrapHorses;
        public final ModConfigSpec.BooleanValue bowFatigue;
        public final ModConfigSpec.BooleanValue crudeBowSkeletons;
        public final ModConfigSpec.BooleanValue skeletonHorsesBurn;
        public final ModConfigSpec.BooleanValue darkForestEvokers;
        public final ModConfigSpec.BooleanValue iglooBasementRemoval;
        public final ModConfigSpec.BooleanValue noVanillaCocoaPods;
        public final ModConfigSpec.BooleanValue paleOakGrowsGloomwood;
        public final ModConfigSpec.BooleanValue voidWater;
        public final ModConfigSpec.IntValue voidWaterTrailLength;
        public final ModConfigSpec.DoubleValue voidWaterTrailDecay;
        public final ModConfigSpec.ConfigValue<List<? extends String>> voidWaterExcludedDimensions;
        public final ModConfigSpec.BooleanValue caveMaps;
        public final ModConfigSpec.BooleanValue caveMapsUseCreationHeight;
        public final ModConfigSpec.IntValue caveMapsFixedHeight;
        public final ModConfigSpec.ConfigValue<List<? extends String>> caveMapsFixedHeights;
        public final ModConfigSpec.ConfigValue<List<? extends String>> caveMapsCreationHeightDimensions;
        public final ModConfigSpec.BooleanValue gravestoneDecayWithTime;
        public final ModConfigSpec.BooleanValue gravestoneDecayWithDeaths;
        public final ModConfigSpec.IntValue gravestoneDecayHours;
        public final ModConfigSpec.IntValue gravestoneDamageToBreak;
        public final ModConfigSpec.IntValue comfortHealInterval;
        public final ModConfigSpec.IntValue comfortRestingHealInterval;
        public final ModConfigSpec.IntValue comfortSoupSecondsPerNutrition;
        public final ModConfigSpec.IntValue comfortSoupMaxSeconds;
        public final ModConfigSpec.IntValue comfortSleepSeconds;
        public final ModConfigSpec.BooleanValue gravestoneStoreExperience;
        public final ModConfigSpec.BooleanValue gravestoneKeepAllExperience;
        public final ModConfigSpec.BooleanValue gravestoneExperienceDecay;
        public final ModConfigSpec.BooleanValue staticHorseStats;
        public final ModConfigSpec.DoubleValue staticHorseSpeed;
        public final ModConfigSpec.DoubleValue staticHorseJump;
        public final ModConfigSpec.DoubleValue staticDonkeySpeed;
        public final ModConfigSpec.DoubleValue staticDonkeyJump;
        public final ModConfigSpec.DoubleValue staticMuleSpeed;
        public final ModConfigSpec.DoubleValue staticMuleJump;

        public final Map<String, ModConfigSpec.BooleanValue> features = new LinkedHashMap<>();

        public final ModConfigSpec.BooleanValue disableRecipeBookTracking;
        public final ModConfigSpec.BooleanValue parallelLocate;
        public final ModConfigSpec.BooleanValue lodGeneration;
        public final ModConfigSpec.IntValue lodGenerationMaxRadius;
        public final ModConfigSpec.IntValue lodGenerationThreads;
        public final ModConfigSpec.IntValue lodGenerationSendRate;

        public Common(ModConfigSpec.Builder builder) {
            builder.push("compat");

            oldWisteriaTrees = builder
                    .comment(tooltip("oldWisteriaTrees"))
                    .translation("option.spicedcider.oldWisteriaTrees")
                    .define("oldWisteriaTrees", false);

            slimeTimeDisableItemMerging = builder
                    .comment(tooltip("slimeTimeDisableItemMerging"))
                    .translation("option.spicedcider.slimeTimeDisableItemMerging")
                    .define("slimeTimeDisableItemMerging", false);

            easyAnvilsFreeRepairs = builder
                    .comment(tooltip("easyAnvilsFreeRepairs"))
                    .translation("option.spicedcider.easyAnvilsFreeRepairs")
                    .define("easyAnvilsFreeRepairs", false);

            toolRepairMaterialCounts = builder
                    .comment(tooltip("toolRepairMaterialCounts"))
                    .translation("option.spicedcider.toolRepairMaterialCounts")
                    .define("toolRepairMaterialCounts", false);

            fireRingNoPotions = builder
                    .comment(tooltip("fireRingNoPotions"))
                    .translation("option.spicedcider.fireRingNoPotions")
                    .define("fireRingNoPotions", false);

            witchStewBasin = builder
                    .comment(tooltip("witchStewBasin"))
                    .translation("option.spicedcider.witchStewBasin")
                    .define("witchStewBasin", false);

            copperGolemBuildingDisabled = builder
                    .comment(tooltip("copperGolemBuildingDisabled"))
                    .translation("option.spicedcider.copperGolemBuildingDisabled")
                    .define("copperGolemBuildingDisabled", false);

            whereWindsBlowNoBonemealGrass = builder
                    .comment(tooltip("whereWindsBlowNoBonemealGrass"))
                    .translation("option.spicedcider.whereWindsBlowNoBonemealGrass")
                    .define("whereWindsBlowNoBonemealGrass", false);

            backpackInventoryRows = builder
                    .comment(tooltip("backpackInventoryRows"))
                    .translation("option.spicedcider.backpackInventoryRows")
                    .define("backpackInventoryRows", false);

            backpackAcceptAnything = builder
                    .comment(tooltip("backpackAcceptAnything"))
                    .translation("option.spicedcider.backpackAcceptAnything")
                    .define("backpackAcceptAnything", false);

            incrementalMiningVanillaSpeed = builder
                    .comment(tooltip("incrementalMiningVanillaSpeed"))
                    .translation("option.spicedcider.incrementalMiningVanillaSpeed")
                    .define("incrementalMiningVanillaSpeed", false);

            dustBunnySpawners = builder
                    .comment(tooltip("dustBunnySpawners"))
                    .translation("option.spicedcider.dustBunnySpawners")
                    .define("dustBunnySpawners", false);

            tapRedstone = builder
                    .comment(tooltip("tapRedstone"))
                    .translation("option.spicedcider.tapRedstone")
                    .define("tapRedstone", false);

            tapFaucetFluids = builder
                    .comment(tooltip("tapFaucetFluids"))
                    .translation("option.spicedcider.tapFaucetFluids")
                    .define("tapFaucetFluids", false);

            pineconesOnTrees = builder
                    .comment(tooltip("pineconesOnTrees"))
                    .translation("option.spicedcider.pineconesOnTrees")
                    .define("pineconesOnTrees", false);

            copperGolemNoChest = builder
                    .comment(tooltip("copperGolemNoChest"))
                    .translation("option.spicedcider.copperGolemNoChest")
                    .define("copperGolemNoChest", false);

            jackOStrawRotation = builder
                    .comment(tooltip("jackOStrawRotation"))
                    .translation("option.spicedcider.jackOStrawRotation")
                    .define("jackOStrawRotation", false);

            treeChoppingStamina = builder
                    .comment(tooltip("treeChoppingStamina"))
                    .translation("option.spicedcider.treeChoppingStamina")
                    .define("treeChoppingStamina", false);

            ledgeClimbRework = builder
                    .comment(tooltip("ledgeClimbRework"))
                    .translation("option.spicedcider.ledgeClimbRework")
                    .define("ledgeClimbRework", false);

            ledgeClimbStamina = builder
                    .comment(tooltip("ledgeClimbStamina"))
                    .translation("option.spicedcider.ledgeClimbStamina")
                    .define("ledgeClimbStamina", false);

            sittingStaminaRegen = builder
                    .comment(tooltip("sittingStaminaRegen"))
                    .translation("option.spicedcider.sittingStaminaRegen")
                    .define("sittingStaminaRegen", false);

            noFoodStamina = builder
                    .comment(tooltip("noFoodStamina"))
                    .translation("option.spicedcider.noFoodStamina")
                    .define("noFoodStamina", false);

            mealIngredientsKeepHealth = builder
                    .comment(tooltip("mealIngredientsKeepHealth"))
                    .translation("option.spicedcider.mealIngredientsKeepHealth")
                    .define("mealIngredientsKeepHealth", false);

            nourishmentDurationPercent = builder
                    .comment(tooltip("nourishmentDurationPercent"))
                    .translation("option.spicedcider.nourishmentDurationPercent")
                    .defineInRange("nourishmentDurationPercent", 100, 1, 100);

            quarkStoolSeats = builder
                    .comment(tooltip("quarkStoolSeats"))
                    .translation("option.spicedcider.quarkStoolSeats")
                    .define("quarkStoolSeats", false);

            basinCampfireHeat = builder
                    .comment(tooltip("basinCampfireHeat"))
                    .translation("option.spicedcider.basinCampfireHeat")
                    .define("basinCampfireHeat", false);

            calmerHerdPanic = builder
                    .comment(tooltip("calmerHerdPanic"))
                    .translation("option.spicedcider.calmerHerdPanic")
                    .define("calmerHerdPanic", false);

            sneakUpOnRabbits = builder
                    .comment(tooltip("sneakUpOnRabbits"))
                    .translation("option.spicedcider.sneakUpOnRabbits")
                    .define("sneakUpOnRabbits", false);

            weakerLeapers = builder
                    .comment(tooltip("weakerLeapers"))
                    .translation("option.spicedcider.weakerLeapers")
                    .define("weakerLeapers", false);

            chalkSymbolsAboveHotbar = builder
                    .comment(tooltip("chalkSymbolsAboveHotbar"))
                    .translation("option.spicedcider.chalkSymbolsAboveHotbar")
                    .define("chalkSymbolsAboveHotbar", false);

            vanillaBackportLightTweaks = builder
                    .comment(tooltip("vanillaBackportLightTweaks"))
                    .translation("option.spicedcider.vanillaBackportLightTweaks")
                    .define("vanillaBackportLightTweaks", false);

            creakingBuff = builder
                    .comment(tooltip("creakingBuff"))
                    .translation("option.spicedcider.creakingBuff")
                    .define("creakingBuff", false);

            goldenDandelionTempting = builder
                    .comment(tooltip("goldenDandelionTempting"))
                    .translation("option.spicedcider.goldenDandelionTempting")
                    .define("goldenDandelionTempting", false);

            oceanRuinPrismarineChests = builder
                    .comment(tooltip("oceanRuinPrismarineChests"))
                    .translation("option.spicedcider.oceanRuinPrismarineChests")
                    .define("oceanRuinPrismarineChests", false);

            builder.pop();
            builder.push("features");

            for (String feature : FEATURES) {
                features.put(feature, builder
                        .comment(tooltip(feature))
                        .translation("option.spicedcider." + feature)
                        .define(feature, !FEATURES_OFF_BY_DEFAULT.contains(feature)));
            }

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

            brokenToolsOnCuttingBoards = builder
                    .comment(tooltip("brokenToolsOnCuttingBoards"))
                    .translation("option.spicedcider.brokenToolsOnCuttingBoards")
                    .define("brokenToolsOnCuttingBoards", false);

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

            harmlessGhastFireballs = builder
                    .comment(tooltip("harmlessGhastFireballs"))
                    .translation("option.spicedcider.harmlessGhastFireballs")
                    .define("harmlessGhastFireballs", false);

            logsNeedAxe = builder
                    .comment(tooltip("logsNeedAxe"))
                    .translation("option.spicedcider.logsNeedAxe")
                    .define("logsNeedAxe", false);

            stepUp = builder
                    .comment(tooltip("stepUp"))
                    .translation("option.spicedcider.stepUp")
                    .define("stepUp", false);

            hammersCannotMine = builder
                    .comment(tooltip("hammersCannotMine"))
                    .translation("option.spicedcider.hammersCannotMine")
                    .define("hammersCannotMine", false);

            treeBark = builder
                    .comment(tooltip("treeBark"))
                    .translation("option.spicedcider.treeBark")
                    .define("treeBark", false);

            mossNeedsHoe = builder
                    .comment(tooltip("mossNeedsHoe"))
                    .translation("option.spicedcider.mossNeedsHoe")
                    .define("mossNeedsHoe", false);

            blockDyeing = builder
                    .comment(tooltip("blockDyeing"))
                    .translation("option.spicedcider.blockDyeing")
                    .define("blockDyeing", true);

            composterWorms = builder
                    .comment(tooltip("composterWorms"))
                    .translation("option.spicedcider.composterWorms")
                    .define("composterWorms", true);

            azaleaTreePrevention = builder
                    .comment(tooltip("azaleaTreePrevention"))
                    .translation("option.spicedcider.azaleaTreePrevention")
                    .define("azaleaTreePrevention", false);

            noLeafSpawns = builder
                    .comment(tooltip("noLeafSpawns"))
                    .translation("option.spicedcider.noLeafSpawns")
                    .define("noLeafSpawns", true);

            zombieTorches = builder
                    .comment(tooltip("zombieTorches"))
                    .translation("option.spicedcider.zombieTorches")
                    .define("zombieTorches", false);

            darkMushrooms = builder
                    .comment(tooltip("darkMushrooms"))
                    .translation("option.spicedcider.darkMushrooms")
                    .define("darkMushrooms", false);

            piglinWeaponSwap = builder
                    .comment(tooltip("piglinWeaponSwap"))
                    .translation("option.spicedcider.piglinWeaponSwap")
                    .define("piglinWeaponSwap", false);

            soulCampfireHaunting = builder
                    .comment(tooltip("soulCampfireHaunting"))
                    .translation("option.spicedcider.soulCampfireHaunting")
                    .define("soulCampfireHaunting", true);

            throwablePotionCooldown = builder
                    .comment(tooltip("throwablePotionCooldown"))
                    .translation("option.spicedcider.throwablePotionCooldown")
                    .define("throwablePotionCooldown", false);

            privateAdvancementMessages = builder
                    .comment(tooltip("privateAdvancementMessages"))
                    .translation("option.spicedcider.privateAdvancementMessages")
                    .define("privateAdvancementMessages", false);

            accurateSignHitboxes = builder
                    .comment(tooltip("accurateSignHitboxes"))
                    .translation("option.spicedcider.accurateSignHitboxes")
                    .define("accurateSignHitboxes", true);

            bundleCapacity = builder
                    .comment(tooltip("bundleCapacity"))
                    .translation("option.spicedcider.bundleCapacity")
                    .defineInRange("bundleCapacity", 64, 1, 1024);

            fallDamageReduction = builder
                    .comment(tooltip("fallDamageReduction"))
                    .translation("option.spicedcider.fallDamageReduction")
                    .defineInRange("fallDamageReduction", 0, 0, 20);

            noNestedBundles = builder
                    .comment(tooltip("noNestedBundles"))
                    .translation("option.spicedcider.noNestedBundles")
                    .define("noNestedBundles", false);

            rangedSkeletons = builder
                    .comment(tooltip("rangedSkeletons"))
                    .translation("option.spicedcider.rangedSkeletons")
                    .define("rangedSkeletons", false);

            singleBlazeFireball = builder
                    .comment(tooltip("singleBlazeFireball"))
                    .translation("option.spicedcider.singleBlazeFireball")
                    .define("singleBlazeFireball", false);

            noSkeletonTrapHorses = builder
                    .comment(tooltip("noSkeletonTrapHorses"))
                    .translation("option.spicedcider.noSkeletonTrapHorses")
                    .define("noSkeletonTrapHorses", false);

            bowFatigue = builder
                    .comment(tooltip("bowFatigue"))
                    .translation("option.spicedcider.bowFatigue")
                    .define("bowFatigue", false);

            crudeBowSkeletons = builder
                    .comment(tooltip("crudeBowSkeletons"))
                    .translation("option.spicedcider.crudeBowSkeletons")
                    .define("crudeBowSkeletons", false);

            skeletonHorsesBurn = builder
                    .comment(tooltip("skeletonHorsesBurn"))
                    .translation("option.spicedcider.skeletonHorsesBurn")
                    .define("skeletonHorsesBurn", false);

            darkForestEvokers = builder
                    .comment(tooltip("darkForestEvokers"))
                    .translation("option.spicedcider.darkForestEvokers")
                    .define("darkForestEvokers", false);

            iglooBasementRemoval = builder
                    .comment(tooltip("iglooBasementRemoval"))
                    .translation("option.spicedcider.iglooBasementRemoval")
                    .define("iglooBasementRemoval", false);

            noVanillaCocoaPods = builder
                    .comment(tooltip("noVanillaCocoaPods"))
                    .translation("option.spicedcider.noVanillaCocoaPods")
                    .define("noVanillaCocoaPods", false);

            paleOakGrowsGloomwood = builder
                    .comment(tooltip("paleOakGrowsGloomwood"))
                    .translation("option.spicedcider.paleOakGrowsGloomwood")
                    .define("paleOakGrowsGloomwood", false);

            builder.pop();
            builder.push("voidWater");

            voidWater = builder
                    .comment(tooltip("voidWater"))
                    .translation("option.spicedcider.voidWater")
                    .define("voidWater", true);

            voidWaterTrailLength = builder
                    .comment(tooltip("voidWaterTrailLength"))
                    .translation("option.spicedcider.voidWaterTrailLength")
                    .defineInRange("voidWaterTrailLength", 16, 0, 255);

            voidWaterTrailDecay = builder
                    .comment(tooltip("voidWaterTrailDecay"))
                    .translation("option.spicedcider.voidWaterTrailDecay")
                    .defineInRange("voidWaterTrailDecay", 2.0, 0.05, 16.0);

            voidWaterExcludedDimensions = builder
                    .comment(tooltip("voidWaterExcludedDimensions"))
                    .translation("option.spicedcider.voidWaterExcludedDimensions")
                    .defineListAllowEmpty("voidWaterExcludedDimensions", List.of("minecraft:the_end"), () -> "minecraft:the_end",
                            element -> element instanceof String id && ResourceLocation.tryParse(id) != null);

            builder.pop();
            builder.push("caveMaps");

            caveMaps = builder
                    .comment(tooltip("caveMaps"))
                    .translation("option.spicedcider.caveMaps")
                    .define("caveMaps", true);

            caveMapsUseCreationHeight = builder
                    .comment(tooltip("caveMapsUseCreationHeight"))
                    .translation("option.spicedcider.caveMapsUseCreationHeight")
                    .define("caveMapsUseCreationHeight", true);

            caveMapsFixedHeight = builder
                    .comment(tooltip("caveMapsFixedHeight"))
                    .translation("option.spicedcider.caveMapsFixedHeight")
                    .defineInRange("caveMapsFixedHeight", 100, -64, 320);

            caveMapsFixedHeights = builder
                    .comment(tooltip("caveMapsFixedHeights"))
                    .translation("option.spicedcider.caveMapsFixedHeights")
                    .defineListAllowEmpty("caveMapsFixedHeights", List.of("minecraft:the_nether=40"), () -> "minecraft:the_nether=40",
                            element -> element instanceof String entry && CaveMapHeights.parseFixed(entry) != null);

            caveMapsCreationHeightDimensions = builder
                    .comment(tooltip("caveMapsCreationHeightDimensions"))
                    .translation("option.spicedcider.caveMapsCreationHeightDimensions")
                    .defineListAllowEmpty("caveMapsCreationHeightDimensions", List.of(), () -> "minecraft:the_nether",
                            element -> element instanceof String id && ResourceLocation.tryParse(id) != null);

            builder.pop();
            builder.push("graves");

            gravestoneDecayWithTime = builder
                    .comment(tooltip("gravestoneDecayWithTime"))
                    .translation("option.spicedcider.gravestoneDecayWithTime")
                    .define("gravestoneDecayWithTime", true);

            gravestoneDecayWithDeaths = builder
                    .comment(tooltip("gravestoneDecayWithDeaths"))
                    .translation("option.spicedcider.gravestoneDecayWithDeaths")
                    .define("gravestoneDecayWithDeaths", true);

            gravestoneDecayHours = builder
                    .comment(tooltip("gravestoneDecayHours"))
                    .translation("option.spicedcider.gravestoneDecayHours")
                    .defineInRange("gravestoneDecayHours", 8, 1, 168);

            gravestoneDamageToBreak = builder
                    .comment(tooltip("gravestoneDamageToBreak"))
                    .translation("option.spicedcider.gravestoneDamageToBreak")
                    .defineInRange("gravestoneDamageToBreak", 3, 1, 20);

            gravestoneStoreExperience = builder
                    .comment(tooltip("gravestoneStoreExperience"))
                    .translation("option.spicedcider.gravestoneStoreExperience")
                    .define("gravestoneStoreExperience", true);

            gravestoneKeepAllExperience = builder
                    .comment(tooltip("gravestoneKeepAllExperience"))
                    .translation("option.spicedcider.gravestoneKeepAllExperience")
                    .define("gravestoneKeepAllExperience", false);

            gravestoneExperienceDecay = builder
                    .comment(tooltip("gravestoneExperienceDecay"))
                    .translation("option.spicedcider.gravestoneExperienceDecay")
                    .define("gravestoneExperienceDecay", false);

            builder.pop();
            builder.push("comfort");

            comfortHealInterval = builder
                    .comment(tooltip("comfortHealInterval"))
                    .translation("option.spicedcider.comfortHealInterval")
                    .defineInRange("comfortHealInterval", 80, 1, 1200);

            comfortRestingHealInterval = builder
                    .comment(tooltip("comfortRestingHealInterval"))
                    .translation("option.spicedcider.comfortRestingHealInterval")
                    .defineInRange("comfortRestingHealInterval", 40, 1, 1200);

            comfortSoupSecondsPerNutrition = builder
                    .comment(tooltip("comfortSoupSecondsPerNutrition"))
                    .translation("option.spicedcider.comfortSoupSecondsPerNutrition")
                    .defineInRange("comfortSoupSecondsPerNutrition", 0, 0, 600);

            comfortSoupMaxSeconds = builder
                    .comment(tooltip("comfortSoupMaxSeconds"))
                    .translation("option.spicedcider.comfortSoupMaxSeconds")
                    .defineInRange("comfortSoupMaxSeconds", 180, 0, 3600);

            comfortSleepSeconds = builder
                    .comment(tooltip("comfortSleepSeconds"))
                    .translation("option.spicedcider.comfortSleepSeconds")
                    .defineInRange("comfortSleepSeconds", 0, 0, 3600);

            builder.pop();
            builder.push("horses");

            staticHorseStats = builder
                    .comment(tooltip("staticHorseStats"))
                    .translation("option.spicedcider.staticHorseStats")
                    .define("staticHorseStats", false);

            staticHorseSpeed = builder
                    .comment(tooltip("staticHorseSpeed"))
                    .translation("option.spicedcider.staticHorseSpeed")
                    .defineInRange("staticHorseSpeed", 0.3375, 0.05, 1.0);

            staticHorseJump = builder
                    .comment(tooltip("staticHorseJump"))
                    .translation("option.spicedcider.staticHorseJump")
                    .defineInRange("staticHorseJump", 1.0, 0.1, 2.0);

            staticDonkeySpeed = builder
                    .comment(tooltip("staticDonkeySpeed"))
                    .translation("option.spicedcider.staticDonkeySpeed")
                    .defineInRange("staticDonkeySpeed", 0.175, 0.05, 1.0);

            staticDonkeyJump = builder
                    .comment(tooltip("staticDonkeyJump"))
                    .translation("option.spicedcider.staticDonkeyJump")
                    .defineInRange("staticDonkeyJump", 0.5, 0.1, 2.0);

            staticMuleSpeed = builder
                    .comment(tooltip("staticMuleSpeed"))
                    .translation("option.spicedcider.staticMuleSpeed")
                    .defineInRange("staticMuleSpeed", 0.175, 0.05, 1.0);

            staticMuleJump = builder
                    .comment(tooltip("staticMuleJump"))
                    .translation("option.spicedcider.staticMuleJump")
                    .defineInRange("staticMuleJump", 0.5, 0.1, 2.0);

            builder.pop();
            builder.push("wind");

            wind = builder
                    .comment(tooltip("wind"))
                    .translation("option.spicedcider.wind")
                    .define("wind", false);

            windCreateWindmills = builder
                    .comment(tooltip("windCreateWindmills"))
                    .translation("option.spicedcider.windCreateWindmills")
                    .define("windCreateWindmills", true);

            windVaneSignal = builder
                    .comment(tooltip("windVaneSignal"))
                    .translation("option.spicedcider.windVaneSignal")
                    .define("windVaneSignal", true);

            windGliders = builder
                    .comment(tooltip("windGliders"))
                    .translation("option.spicedcider.windGliders")
                    .define("windGliders", true);

            builder.pop();
            builder.push("performance");

            disableRecipeBookTracking = builder
                    .comment(tooltip("disableRecipeBookTracking"))
                    .translation("option.spicedcider.disableRecipeBookTracking")
                    .define("disableRecipeBookTracking", true);

            parallelLocate = builder
                    .comment(tooltip("parallelLocate"))
                    .translation("option.spicedcider.parallelLocate")
                    .define("parallelLocate", true);

            revealThreateningMonsters = builder
                    .comment(tooltip("revealThreateningMonsters"))
                    .translation("option.spicedcider.revealThreateningMonsters")
                    .define("revealThreateningMonsters", false);

            builder.pop();
            builder.push("lod");

            lodGeneration = builder
                    .comment(tooltip("lodGeneration"))
                    .translation("option.spicedcider.lodGeneration")
                    .define("lodGeneration", true);

            lodGenerationMaxRadius = builder
                    .comment(tooltip("lodGenerationMaxRadius"))
                    .translation("option.spicedcider.lodGenerationMaxRadius")
                    .defineInRange("lodGenerationMaxRadius", 128, 8, 512);

            lodGenerationThreads = builder
                    .comment(tooltip("lodGenerationThreads"))
                    .translation("option.spicedcider.lodGenerationThreads")
                    .defineInRange("lodGenerationThreads", 0, 0, 16);

            lodGenerationSendRate = builder
                    .comment(tooltip("lodGenerationSendRate"))
                    .translation("option.spicedcider.lodGenerationSendRate")
                    .defineInRange("lodGenerationSendRate", 100, 10, 2000);

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
        public final ModConfigSpec.BooleanValue customOptionsScreen;
        public final ModConfigSpec.BooleanValue anvilHideNameField;
        public final ModConfigSpec.BooleanValue bedScreenTips;
        public final ModConfigSpec.BooleanValue moveRecipeBookButton;
        public final ModConfigSpec.BooleanValue inventoryModelRotation;
        public final ModConfigSpec.BooleanValue hideOwnEffectParticles;
        public final ModConfigSpec.BooleanValue hideArmorBar;
        public final ModConfigSpec.BooleanValue matchHeldLightColors;
        public final ModConfigSpec.BooleanValue matchChainLightColors;
        public final ModConfigSpec.BooleanValue chainDecorationSway;
        public final ModConfigSpec.BooleanValue windParticles;
        public final ModConfigSpec.IntValue windParticleStrength;
        public final ModConfigSpec.BooleanValue windMotes;
        public final ModConfigSpec.BooleanValue hangingFruitSway;
        public final ModConfigSpec.BooleanValue softerBlindnessAndDarkness;
        public final ModConfigSpec.BooleanValue hideSmithingTemplateTooltips;
        public final ModConfigSpec.BooleanValue hideSackContentsTooltip;

        public final ModConfigSpec.BooleanValue pictureModeClouds;

        public final ModConfigSpec.BooleanValue vistaMirrorPerfFixes;
        public final ModConfigSpec.IntValue vistaMirrorReflectionDistance;
        public final ModConfigSpec.BooleanValue vistaMirrorFog;
        public final ModConfigSpec.IntValue vistaMirrorFogStart;
        public final ModConfigSpec.IntValue vistaMirrorFogEnd;

        public final ModConfigSpec.IntValue cloudCoverFadeSeconds;
        public final ModConfigSpec.IntValue cloudCoverCommandFadeSeconds;
        public final ModConfigSpec.BooleanValue sunDogs;
        public final ModConfigSpec.BooleanValue snowfallLeafWhitening;
        public final ModConfigSpec.IntValue snowfallLeafWhiteningRadius;

        public final ModConfigSpec.BooleanValue voxyFogOverride;
        public final ModConfigSpec.IntValue voxyFogStart;
        public final ModConfigSpec.IntValue voxyFogEnd;
        public final ModConfigSpec.IntValue voxyWeatherFogDensity;
        public final ModConfigSpec.IntValue voxyCaveFogDensity;
        public final ModConfigSpec.IntValue voxyNetherFogDensity;
        public final ModConfigSpec.BooleanValue netherHeatHaze;
        public final ModConfigSpec.BooleanValue netherMist;
        public final ModConfigSpec.IntValue netherAmbientTint;

        public final ModConfigSpec.BooleanValue voiceChatHideSingleplayerHud;
        public final ModConfigSpec.BooleanValue clutterNoMoreShapeWheel;
        public final ModConfigSpec.BooleanValue remiRightSideTabs;
        public final ModConfigSpec.BooleanValue roughGemsOnGrindstone;
        public final ModConfigSpec.BooleanValue emiHideSmithingCategory;
        public final ModConfigSpec.BooleanValue emiCreateFanRework;
        public final ModConfigSpec.BooleanValue createHideItemDescriptions;
        public final ModConfigSpec.BooleanValue quarkHideHints;
        public final ModConfigSpec.BooleanValue graveLocatorBar;
        public final ModConfigSpec.BooleanValue goldenDandelionParticles;
        public final ModConfigSpec.BooleanValue carvingWheel;
        public final ModConfigSpec.BooleanValue jadeNeedsSpyglassOrMonocle;
        public final ModConfigSpec.BooleanValue stepUpEnabled;
        public final ModConfigSpec.BooleanValue smoothStepUp;
        public final ModConfigSpec.BooleanValue emiTappingCategory;
        public final ModConfigSpec.BooleanValue emiSluicingCategory;
        public final ModConfigSpec.BooleanValue emiBarteringCategory;
        public final ModConfigSpec.BooleanValue emiSniffingCategory;
        public final ModConfigSpec.BooleanValue emiArchaeologyCategory;
        public final ModConfigSpec.BooleanValue emiStructureLootCategory;
        public final ModConfigSpec.BooleanValue emiUrnLootCategory;

        public Client(ModConfigSpec.Builder builder) {
            builder.push("compat");

            voiceChatHideSingleplayerHud = builder
                    .comment(tooltip("voiceChatHideSingleplayerHud"))
                    .translation("option.spicedcider.voiceChatHideSingleplayerHud")
                    .define("voiceChatHideSingleplayerHud", false);

            emiTappingCategory = builder
                    .comment(tooltip("emiTappingCategory"))
                    .translation("option.spicedcider.emiTappingCategory")
                    .define("emiTappingCategory", false);

            emiSluicingCategory = builder
                    .comment(tooltip("emiSluicingCategory"))
                    .translation("option.spicedcider.emiSluicingCategory")
                    .define("emiSluicingCategory", false);

            emiBarteringCategory = builder
                    .comment(tooltip("emiBarteringCategory"))
                    .translation("option.spicedcider.emiBarteringCategory")
                    .define("emiBarteringCategory", false);

            emiSniffingCategory = builder
                    .comment(tooltip("emiSniffingCategory"))
                    .translation("option.spicedcider.emiSniffingCategory")
                    .define("emiSniffingCategory", false);

            emiArchaeologyCategory = builder
                    .comment(tooltip("emiArchaeologyCategory"))
                    .translation("option.spicedcider.emiArchaeologyCategory")
                    .define("emiArchaeologyCategory", false);

            emiStructureLootCategory = builder
                    .comment(tooltip("emiStructureLootCategory"))
                    .translation("option.spicedcider.emiStructureLootCategory")
                    .define("emiStructureLootCategory", false);

            emiUrnLootCategory = builder
                    .comment(tooltip("emiUrnLootCategory"))
                    .translation("option.spicedcider.emiUrnLootCategory")
                    .define("emiUrnLootCategory", false);

            clutterNoMoreShapeWheel = builder
                    .comment(tooltip("clutterNoMoreShapeWheel"))
                    .translation("option.spicedcider.clutterNoMoreShapeWheel")
                    .define("clutterNoMoreShapeWheel", false);

            remiRightSideTabs = builder
                    .comment(tooltip("remiRightSideTabs"))
                    .translation("option.spicedcider.remiRightSideTabs")
                    .define("remiRightSideTabs", false);

            roughGemsOnGrindstone = builder
                    .comment(tooltip("roughGemsOnGrindstone"))
                    .translation("option.spicedcider.roughGemsOnGrindstone")
                    .define("roughGemsOnGrindstone", false);

            emiHideSmithingCategory = builder
                    .comment(tooltip("emiHideSmithingCategory"))
                    .translation("option.spicedcider.emiHideSmithingCategory")
                    .define("emiHideSmithingCategory", false);

            emiCreateFanRework = builder
                    .comment(tooltip("emiCreateFanRework"))
                    .translation("option.spicedcider.emiCreateFanRework")
                    .define("emiCreateFanRework", false);

            createHideItemDescriptions = builder
                    .comment(tooltip("createHideItemDescriptions"))
                    .translation("option.spicedcider.createHideItemDescriptions")
                    .define("createHideItemDescriptions", false);

            quarkHideHints = builder
                    .comment(tooltip("quarkHideHints"))
                    .translation("option.spicedcider.quarkHideHints")
                    .define("quarkHideHints", false);

            graveLocatorBar = builder
                    .comment(tooltip("graveLocatorBar"))
                    .translation("option.spicedcider.graveLocatorBar")
                    .define("graveLocatorBar", false);

            goldenDandelionParticles = builder
                    .comment(tooltip("goldenDandelionParticles"))
                    .translation("option.spicedcider.goldenDandelionParticles")
                    .define("goldenDandelionParticles", true);

            carvingWheel = builder
                    .comment(tooltip("carvingWheel"))
                    .translation("option.spicedcider.carvingWheel")
                    .define("carvingWheel", false);

            jadeNeedsSpyglassOrMonocle = builder
                    .comment(tooltip("jadeNeedsSpyglassOrMonocle"))
                    .translation("option.spicedcider.jadeNeedsSpyglassOrMonocle")
                    .define("jadeNeedsSpyglassOrMonocle", false);

            stepUpEnabled = builder
                    .comment(tooltip("stepUpEnabled"))
                    .translation("option.spicedcider.stepUpEnabled")
                    .define("stepUpEnabled", true);

            smoothStepUp = builder
                    .comment(tooltip("smoothStepUp"))
                    .translation("option.spicedcider.smoothStepUp")
                    .define("smoothStepUp", false);

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
                    .define("customDeathSound", false);

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
                    .define("oldProgressScreen", false);

            customOptionsScreen = builder
                    .comment(tooltip("customOptionsScreen"))
                    .translation("option.spicedcider.customOptionsScreen")
                    .define("customOptionsScreen", false);

            anvilHideNameField = builder
                    .comment(tooltip("anvilHideNameField"))
                    .translation("option.spicedcider.anvilHideNameField")
                    .define("anvilHideNameField", false);

            bedScreenTips = builder
                    .comment(tooltip("bedScreenTips"))
                    .translation("option.spicedcider.bedScreenTips")
                    .define("bedScreenTips", false);

            moveRecipeBookButton = builder
                    .comment(tooltip("moveRecipeBookButton"))
                    .translation("option.spicedcider.moveRecipeBookButton")
                    .define("moveRecipeBookButton", false);

            inventoryModelRotation = builder
                    .comment(tooltip("inventoryModelRotation"))
                    .translation("option.spicedcider.inventoryModelRotation")
                    .define("inventoryModelRotation", false);

            hideOwnEffectParticles = builder
                    .comment(tooltip("hideOwnEffectParticles"))
                    .translation("option.spicedcider.hideOwnEffectParticles")
                    .define("hideOwnEffectParticles", false);

            hideArmorBar = builder
                    .comment(tooltip("hideArmorBar"))
                    .translation("option.spicedcider.hideArmorBar")
                    .define("hideArmorBar", false);

            matchHeldLightColors = builder
                    .comment(tooltip("matchHeldLightColors"))
                    .translation("option.spicedcider.matchHeldLightColors")
                    .define("matchHeldLightColors", false);

            matchChainLightColors = builder
                    .comment(tooltip("matchChainLightColors"))
                    .translation("option.spicedcider.matchChainLightColors")
                    .define("matchChainLightColors", false);

            softerBlindnessAndDarkness = builder
                    .comment(tooltip("softerBlindnessAndDarkness"))
                    .translation("option.spicedcider.softerBlindnessAndDarkness")
                    .define("softerBlindnessAndDarkness", false);

            hideSmithingTemplateTooltips = builder
                    .comment(tooltip("hideSmithingTemplateTooltips"))
                    .translation("option.spicedcider.hideSmithingTemplateTooltips")
                    .define("hideSmithingTemplateTooltips", false);

            hideSackContentsTooltip = builder
                    .comment(tooltip("hideSackContentsTooltip"))
                    .translation("option.spicedcider.hideSackContentsTooltip")
                    .define("hideSackContentsTooltip", false);

            pictureModeClouds = builder
                    .comment(tooltip("pictureModeClouds"))
                    .translation("option.spicedcider.pictureModeClouds")
                    .define("pictureModeClouds", true);

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

            vistaMirrorFog = builder
                    .comment(tooltip("vistaMirrorFog"))
                    .translation("option.spicedcider.vistaMirrorFog")
                    .define("vistaMirrorFog", true);

            vistaMirrorFogStart = builder
                    .comment(tooltip("vistaMirrorFogStart"))
                    .translation("option.spicedcider.vistaMirrorFogStart")
                    .defineInRange("vistaMirrorFogStart", 20, 0, 100);

            vistaMirrorFogEnd = builder
                    .comment(tooltip("vistaMirrorFogEnd"))
                    .translation("option.spicedcider.vistaMirrorFogEnd")
                    .defineInRange("vistaMirrorFogEnd", 90, 10, 100);

            builder.pop();
            builder.push("weather");

            cloudCoverFadeSeconds = builder
                    .comment(tooltip("cloudCoverFadeSeconds"))
                    .translation("option.spicedcider.cloudCoverFadeSeconds")
                    .defineInRange("cloudCoverFadeSeconds", 40, 1, 300);

            cloudCoverCommandFadeSeconds = builder
                    .comment(tooltip("cloudCoverCommandFadeSeconds"))
                    .translation("option.spicedcider.cloudCoverCommandFadeSeconds")
                    .defineInRange("cloudCoverCommandFadeSeconds", 5, 0, 60);

            sunDogs = builder
                    .comment(tooltip("sunDogs"))
                    .translation("option.spicedcider.sunDogs")
                    .define("sunDogs", true);

            snowfallLeafWhitening = builder
                    .comment(tooltip("snowfallLeafWhitening"))
                    .translation("option.spicedcider.snowfallLeafWhitening")
                    .define("snowfallLeafWhitening", false);

            snowfallLeafWhiteningRadius = builder
                    .comment(tooltip("snowfallLeafWhiteningRadius"))
                    .translation("option.spicedcider.snowfallLeafWhiteningRadius")
                    .defineInRange("snowfallLeafWhiteningRadius", 8, 2, 32);

            windParticles = builder
                    .comment(tooltip("windParticles"))
                    .translation("option.spicedcider.windParticles")
                    .define("windParticles", true);

            windParticleStrength = builder
                    .comment(tooltip("windParticleStrength"))
                    .translation("option.spicedcider.windParticleStrength")
                    .defineInRange("windParticleStrength", 100, 0, 300);

            windMotes = builder
                    .comment(tooltip("windMotes"))
                    .translation("option.spicedcider.windMotes")
                    .define("windMotes", true);

            hangingFruitSway = builder
                    .comment(tooltip("hangingFruitSway"))
                    .translation("option.spicedcider.hangingFruitSway")
                    .define("hangingFruitSway", true);

            chainDecorationSway = builder
                    .comment(tooltip("chainDecorationSway"))
                    .translation("option.spicedcider.chainDecorationSway")
                    .define("chainDecorationSway", true);

            builder.pop();
            builder.push("voxy");

            voxyFogOverride = builder
                    .comment(tooltip("voxyFogOverride"))
                    .translation("option.spicedcider.voxyFogOverride")
                    .define("voxyFogOverride", true);

            voxyFogStart = builder
                    .comment(tooltip("voxyFogStart"))
                    .translation("option.spicedcider.voxyFogStart")
                    .defineInRange("voxyFogStart", 0, -100, 100);

            voxyFogEnd = builder
                    .comment(tooltip("voxyFogEnd"))
                    .translation("option.spicedcider.voxyFogEnd")
                    .defineInRange("voxyFogEnd", 85, 1, 100);

            voxyWeatherFogDensity = builder
                    .comment(tooltip("voxyWeatherFogDensity"))
                    .translation("option.spicedcider.voxyWeatherFogDensity")
                    .defineInRange("voxyWeatherFogDensity", 90, 0, 100);

            voxyCaveFogDensity = builder
                    .comment(tooltip("voxyCaveFogDensity"))
                    .translation("option.spicedcider.voxyCaveFogDensity")
                    .defineInRange("voxyCaveFogDensity", 50, 0, 100);

            voxyNetherFogDensity = builder
                    .comment(tooltip("voxyNetherFogDensity"))
                    .translation("option.spicedcider.voxyNetherFogDensity")
                    .defineInRange("voxyNetherFogDensity", 50, 0, 100);

            builder.pop();
            builder.push("nether");

            netherHeatHaze = builder
                    .comment(tooltip("netherHeatHaze"))
                    .translation("option.spicedcider.netherHeatHaze")
                    .define("netherHeatHaze", true);

            netherMist = builder
                    .comment(tooltip("netherMist"))
                    .translation("option.spicedcider.netherMist")
                    .define("netherMist", true);

            netherAmbientTint = builder
                    .comment(tooltip("netherAmbientTint"))
                    .translation("option.spicedcider.netherAmbientTint")
                    .defineInRange("netherAmbientTint", 60, 0, 100);

            builder.pop();
        }
    }
}

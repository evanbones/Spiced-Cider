package com.evandev.spicedcider.compat.yacl;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SpicedCiderConfigScreen {

    public static Screen create(Screen parent) {
        SpicedCiderConfig.Startup startup = SpicedCiderConfig.STARTUP;
        SpicedCiderConfig.Common common = SpicedCiderConfig.COMMON;
        SpicedCiderConfig.Client client = SpicedCiderConfig.CLIENT;
        ConfigCategory compat = compat(startup, common, client);

        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("title.spicedcider.config"))
                .save(() -> {
                    SpicedCiderConfig.STARTUP_SPEC.save();
                    SpicedCiderConfig.COMMON_SPEC.save();
                    SpicedCiderConfig.CLIENT_SPEC.save();
                })
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.gameplay"))
                        .tooltip(Component.translatable("category.spicedcider.gameplay.tooltip"))
                        .option(toggle("bedExplosionPrevention", common.bedExplosionPrevention))
                        .option(toggle("respawnAnchorExplosionPrevention", common.respawnAnchorExplosionPrevention))
                        .option(toggle("endCrystalPlaceAnywhere", common.endCrystalPlaceAnywhere))
                        .option(toggle("endCrystalHealing", common.endCrystalHealing))
                        .option(toggle("keepBrokenItems", common.keepBrokenItems))
                        .option(toggle("brokenToolsOnCuttingBoards", common.brokenToolsOnCuttingBoards))
                        .option(toggle("wolvesWearAnyArmor", common.wolvesWearAnyArmor))
                        .option(toggle("unifiedPetArmor", startup.unifiedPetArmor))
                        .option(toggle("spiderRangedWebAttacks", common.spiderRangedWebAttacks))
                        .option(toggle("removeQuasiConnectivity", common.removeQuasiConnectivity))
                        .option(toggle("harmlessGhastFireballs", common.harmlessGhastFireballs))
                        .option(toggle("logsNeedAxe", common.logsNeedAxe))
                        .option(toggle("treeBark", common.treeBark))
                        .option(toggle("mossNeedsHoe", common.mossNeedsHoe))
                        .option(toggle("blockDyeing", common.blockDyeing))
                        .option(toggle("composterWorms", common.composterWorms))
                        .option(toggle("azaleaTreePrevention", common.azaleaTreePrevention))
                        .option(toggle("zombieTorches", common.zombieTorches))
                        .option(toggle("piglinWeaponSwap", common.piglinWeaponSwap))
                        .option(toggle("throwablePotionCooldown", common.throwablePotionCooldown))
                        .option(toggle("accurateSignHitboxes", common.accurateSignHitboxes))
                        .option(toggle("iglooBasementRemoval", common.iglooBasementRemoval))
                        .option(toggle("leavesNotSuffocating", startup.leavesNotSuffocating))
                        .option(toggle("torchflowerLight", startup.torchflowerLight))
                        .option(toggle("skeletonHealthNerf", startup.skeletonHealthNerf))
                        .option(toggle("voidWater", common.voidWater))
                        .option(intSlider("voidWaterTrailLength", common.voidWaterTrailLength, 0, 64, 1))
                        .option(doubleSlider("voidWaterTrailDecay", common.voidWaterTrailDecay, 0.25, 8.0, 0.25))
                        .option(toggle("noVanillaCocoaPods", common.noVanillaCocoaPods))
                        .option(toggle("caveMaps", common.caveMaps))
                        .option(toggle("caveMapsUseCreationHeight", common.caveMapsUseCreationHeight))
                        .option(intSlider("caveMapsFixedHeight", common.caveMapsFixedHeight, -64, 320, 1))
                        .build())
                .category(features(common))
                .categoryIf(compat != null, compat)
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.performance"))
                        .tooltip(Component.translatable("category.spicedcider.performance.tooltip"))
                        .option(toggle("disableRecipeBookTracking", common.disableRecipeBookTracking))
                        .option(toggle("parallelLocate", common.parallelLocate))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.client"))
                        .tooltip(Component.translatable("category.spicedcider.client.tooltip"))
                        .option(toggle("randomWorldNaming", client.randomWorldNaming))
                        .option(toggle("customDeathSound", client.customDeathSound))
                        .option(toggle("unmineableBlockSparks", client.unmineableBlockSparks))
                        .option(toggle("customWindowTitle", client.customWindowTitle))
                        .option(stringField("windowTitleFormat", client.windowTitleFormat))
                        .option(toggle("customWindowIcon", startup.customWindowIcon))
                        .option(toggle("oldProgressScreen", client.oldProgressScreen))
                        .option(toggle("customOptionsScreen", client.customOptionsScreen))
                        .option(toggle("anvilHideNameField", client.anvilHideNameField))
                        .option(toggle("bedScreenTips", client.bedScreenTips))
                        .option(toggle("moveRecipeBookButton", client.moveRecipeBookButton))
                        .option(toggle("hideOwnEffectParticles", client.hideOwnEffectParticles))
                        .option(toggle("hideSmithingTemplateTooltips", client.hideSmithingTemplateTooltips))
                        .build())
                .categoryIf(ModList.get().isLoaded("vista"), ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.vista"))
                        .tooltip(Component.translatable("category.spicedcider.vista.tooltip"))
                        .option(toggle("vistaMirrorPerfFixes", client.vistaMirrorPerfFixes))
                        .option(intSlider("vistaMirrorReflectionDistance", client.vistaMirrorReflectionDistance, 8, 2048, 8))
                        .option(toggle("vistaMirrorFog", client.vistaMirrorFog))
                        .option(intSlider("vistaMirrorFogStart", client.vistaMirrorFogStart, 0, 100, 1))
                        .option(intSlider("vistaMirrorFogEnd", client.vistaMirrorFogEnd, 10, 100, 1))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.weather"))
                        .tooltip(Component.translatable("category.spicedcider.weather.tooltip"))
                        .option(intSlider("cloudCoverFadeSeconds", client.cloudCoverFadeSeconds, 1, 300, 1))
                        .option(intSlider("cloudCoverCommandFadeSeconds", client.cloudCoverCommandFadeSeconds, 0, 60, 1))
                        .option(toggle("sunDogs", client.sunDogs))
                        .build())
                .categoryIf(ModList.get().isLoaded("roxy"), ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.voxy"))
                        .tooltip(Component.translatable("category.spicedcider.voxy.tooltip"))
                        .option(toggle("voxyFogOverride", client.voxyFogOverride))
                        .option(percentSlider("voxyFogStart", client.voxyFogStart, -100))
                        .option(percentSlider("voxyFogEnd", client.voxyFogEnd, 1))
                        .option(percentSlider("voxyWeatherFogDensity", client.voxyWeatherFogDensity, 0))
                        .option(percentSlider("voxyCaveFogDensity", client.voxyCaveFogDensity, 0))
                        .option(percentSlider("voxyNetherFogDensity", client.voxyNetherFogDensity, 0))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.nether"))
                        .tooltip(Component.translatable("category.spicedcider.nether.tooltip"))
                        .option(toggle("netherHeatHaze", client.netherHeatHaze))
                        .option(toggle("netherMist", client.netherMist))
                        .option(percentSlider("netherAmbientTint", client.netherAmbientTint, 0))
                        .build())
                .build()
                .generateScreen(parent);
    }

    @Nullable
    private static ConfigCategory compat(SpicedCiderConfig.Startup startup, SpicedCiderConfig.Common common, SpicedCiderConfig.Client client) {
        ModList mods = ModList.get();
        List<OptionGroup> groups = new ArrayList<>();
        group(groups, List.of("architects_palette"),
                toggle("brightCageLanterns", startup.brightCageLanterns));
        group(groups, List.of("blockbox", "everycomp", "moonlight"),
                toggle("blockBoxWoodVariants", startup.blockBoxWoodVariants));
        group(groups, List.of("caverns_and_chasms"),
                toggle("copperGolemBuildingDisabled", common.copperGolemBuildingDisabled));
        group(groups, List.of("clutternomore"),
                toggle("clutterNoMoreShapeWheel", client.clutterNoMoreShapeWheel));
        group(groups, List.of("create"),
                toggle("soulCampfireHaunting", common.soulCampfireHaunting),
                toggle("createHideItemDescriptions", client.createHideItemDescriptions));
        group(groups, List.of("culturaldelights"),
                toggle("drinkIntoxication", startup.drinkIntoxication));
        group(groups, List.of("easyanvils"),
                toggle("easyAnvilsFreeRepairs", common.easyAnvilsFreeRepairs));
        group(groups, List.of("emi"),
                toggle("emiHideSmithingCategory", client.emiHideSmithingCategory),
                toggle("emiBarteringCategory", client.emiBarteringCategory),
                toggle("emiSniffingCategory", client.emiSniffingCategory),
                toggle("emiArchaeologyCategory", client.emiArchaeologyCategory),
                toggle("emiStructureLootCategory", client.emiStructureLootCategory),
                mods.isLoaded("nomansland") ? toggle("emiTappingCategory", client.emiTappingCategory) : null,
                mods.isLoaded("spelunkery") ? toggle("emiSluicingCategory", client.emiSluicingCategory) : null,
                mods.isLoaded("create") ? toggle("emiCreateFanRework", client.emiCreateFanRework) : null);
        group(groups, List.of("environmental"),
                toggle("oldWisteriaTrees", common.oldWisteriaTrees),
                toggle("pineconesOnTrees", common.pineconesOnTrees));
        group(groups, List.of("lodged"),
                toggle("lodgedHideArrowDepth", client.lodgedHideArrowDepth),
                toggle("lodgedHideBleedingRisk", client.lodgedHideBleedingRisk));
        group(groups, List.of("nomansland"),
                toggle("carvingWheel", client.carvingWheel));
        group(groups, List.of("piglinproliferation"),
                toggle("fireRingNoPotions", common.fireRingNoPotions));
        group(groups, List.of("quark"),
                toggle("quarkHideHints", client.quarkHideHints),
                toggle("oceanRuinPrismarineChests", common.oceanRuinPrismarineChests));
        group(groups, List.of("reliable_backpacks"),
                toggle("backpackInventoryRows", common.backpackInventoryRows),
                toggle("backpackAcceptAnything", common.backpackAcceptAnything));
        group(groups, List.of("remi"),
                toggle("remiRightSideTabs", client.remiRightSideTabs));
        group(groups, List.of("slime_time"),
                toggle("slimeTimeDisableItemMerging", common.slimeTimeDisableItemMerging));
        group(groups, List.of("tide"),
                toggle("tideSpawnEggs", startup.tideSpawnEggs));
        group(groups, List.of("vanillabackport"),
                toggle("sulfurCavesRework", startup.sulfurCavesRework),
                toggle("vanillaBackportLightTweaks", common.vanillaBackportLightTweaks),
                toggle("creakingBuff", common.creakingBuff),
                toggle("copperGolemNoChest", common.copperGolemNoChest),
                toggle("paleOakGrowsGloomwood", common.paleOakGrowsGloomwood));
        group(groups, List.of("tiny_takeover_backport"),
                toggle("goldenDandelionTempting", common.goldenDandelionTempting),
                toggle("goldenDandelionParticles", client.goldenDandelionParticles));
        group(groups, List.of("voicechat"),
                toggle("voiceChatHideSingleplayerHud", client.voiceChatHideSingleplayerHud));
        group(groups, List.of("where_winds_blow"),
                toggle("whereWindsBlowNoBonemealGrass", common.whereWindsBlowNoBonemealGrass));
        group(groups, List.of("bettermineshafts", "spelunkery"),
                toggle("dustBunnySpawners", common.dustBunnySpawners));
        if (groups.isEmpty()) return null;
        return ConfigCategory.createBuilder()
                .name(Component.translatable("category.spicedcider.compat"))
                .tooltip(Component.translatable("category.spicedcider.compat.tooltip"))
                .groups(groups)
                .build();
    }

    private static void group(List<OptionGroup> groups, List<String> modIds, @Nullable Option<?>... options) {
        ModList mods = ModList.get();
        if (!modIds.stream().allMatch(mods::isLoaded)) return;
        String name = modIds.stream()
                .map(id -> mods.getModContainerById(id).map(container -> container.getModInfo().getDisplayName()).orElse(id))
                .collect(Collectors.joining(" + "));
        groups.add(OptionGroup.createBuilder()
                .name(Component.literal(name))
                .options(Arrays.stream(options).filter(Objects::nonNull).toList())
                .collapsed(false)
                .build());
    }

    private static ConfigCategory features(SpicedCiderConfig.Common common) {
        ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                .name(Component.translatable("category.spicedcider.features"))
                .tooltip(Component.translatable("category.spicedcider.features.tooltip"));
        common.features.forEach((name, value) -> {
            if (!name.equals("grapplingHooks") || ModList.get().isLoaded("tide")) builder.option(toggle(name, value));
        });
        return builder.build();
    }

    private static Option<Boolean> toggle(String key, ModConfigSpec.BooleanValue value) {
        Consumer<Boolean> setter = value::set;

        return Option.<Boolean>createBuilder()
                .name(Component.translatable("option.spicedcider." + key))
                .description(OptionDescription.of(Component.translatable("option.spicedcider." + key + ".tooltip")))
                .binding(value.getDefault(), value, setter)
                .controller(TickBoxControllerBuilder::create)
                .build();
    }

    private static Option<String> stringField(String key, ModConfigSpec.ConfigValue<String> value) {
        return Option.<String>createBuilder()
                .name(Component.translatable("option.spicedcider." + key))
                .description(OptionDescription.of(Component.translatable("option.spicedcider." + key + ".tooltip")))
                .binding(value.getDefault(), value, value::set)
                .controller(StringControllerBuilder::create)
                .build();
    }

    private static Option<Integer> intSlider(String key, ModConfigSpec.IntValue value, int min, int max, int step) {
        return Option.<Integer>createBuilder()
                .name(Component.translatable("option.spicedcider." + key))
                .description(OptionDescription.of(Component.translatable("option.spicedcider." + key + ".tooltip")))
                .binding(value.getDefault(), value, value::set)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(min, max).step(step))
                .build();
    }

    private static Option<Integer> percentSlider(String key, ModConfigSpec.IntValue value, int min) {
        return Option.<Integer>createBuilder()
                .name(Component.translatable("option.spicedcider." + key))
                .description(OptionDescription.of(Component.translatable("option.spicedcider." + key + ".tooltip")))
                .binding(value.getDefault(), value, value::set)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(min, 100).step(1)
                        .formatValue(v -> Component.literal(v + "%")))
                .build();
    }

    private static Option<Double> doubleSlider(String key, ModConfigSpec.DoubleValue value, double min, double max, double step) {
        return Option.<Double>createBuilder()
                .name(Component.translatable("option.spicedcider." + key))
                .description(OptionDescription.of(Component.translatable("option.spicedcider." + key + ".tooltip")))
                .binding(value.getDefault(), value, value::set)
                .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(min, max).step(step))
                .build();
    }
}

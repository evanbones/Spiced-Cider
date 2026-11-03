package com.evandev.spicedcider.compat.yacl;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Consumer;

public class SpicedCiderConfigScreen {

    public static Screen create(Screen parent) {
        SpicedCiderConfig.Startup startup = SpicedCiderConfig.STARTUP;
        SpicedCiderConfig.Common common = SpicedCiderConfig.COMMON;
        SpicedCiderConfig.Client client = SpicedCiderConfig.CLIENT;

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
                        .option(toggle("caveMaps", common.caveMaps))
                        .option(toggle("caveMapsUseCreationHeight", common.caveMapsUseCreationHeight))
                        .option(intSlider("caveMapsFixedHeight", common.caveMapsFixedHeight, -64, 320, 1))
                        .build())
                .category(features(common))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.compat"))
                        .tooltip(Component.translatable("category.spicedcider.compat.tooltip"))
                        .optionIf(ModList.get().isLoaded("environmental"),
                                toggle("oldWisteriaTrees", common.oldWisteriaTrees))
                        .optionIf(ModList.get().isLoaded("blockbox") && ModList.get().isLoaded("everycomp") && ModList.get().isLoaded("moonlight"),
                                toggle("blockBoxWoodVariants", startup.blockBoxWoodVariants))
                        .optionIf(ModList.get().isLoaded("tide"),
                                toggle("tideSpawnEggs", startup.tideSpawnEggs))
                        .optionIf(ModList.get().isLoaded("culturaldelights"),
                                toggle("drinkIntoxication", startup.drinkIntoxication))
                        .optionIf(ModList.get().isLoaded("create"),
                                toggle("soulCampfireHaunting", common.soulCampfireHaunting))
                        .optionIf(ModList.get().isLoaded("slime_time"),
                                toggle("slimeTimeDisableItemMerging", common.slimeTimeDisableItemMerging))
                        .optionIf(ModList.get().isLoaded("lodged"),
                                toggle("lodgedHideArrowDepth", client.lodgedHideArrowDepth))
                        .optionIf(ModList.get().isLoaded("lodged"),
                                toggle("lodgedHideBleedingRisk", client.lodgedHideBleedingRisk))
                        .optionIf(ModList.get().isLoaded("easyanvils"),
                                toggle("easyAnvilsFreeRepairs", common.easyAnvilsFreeRepairs))
                        .optionIf(ModList.get().isLoaded("piglinproliferation"),
                                toggle("fireRingNoPotions", common.fireRingNoPotions))
                        .optionIf(ModList.get().isLoaded("caverns_and_chasms"),
                                toggle("copperGolemBuildingDisabled", common.copperGolemBuildingDisabled))
                        .optionIf(ModList.get().isLoaded("where_winds_blow"),
                                toggle("whereWindsBlowNoBonemealGrass", common.whereWindsBlowNoBonemealGrass))
                        .optionIf(ModList.get().isLoaded("voicechat"),
                                toggle("voiceChatHideSingleplayerHud", client.voiceChatHideSingleplayerHud))
                        .optionIf(ModList.get().isLoaded("clutternomore"),
                                toggle("clutterNoMoreShapeWheel", client.clutterNoMoreShapeWheel))
                        .build())
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
                        .option(toggle("oldProgressScreen", client.oldProgressScreen))
                        .option(toggle("customOptionsScreen", client.customOptionsScreen))
                        .build())
                .categoryIf(ModList.get().isLoaded("vista"), ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.vista"))
                        .tooltip(Component.translatable("category.spicedcider.vista.tooltip"))
                        .option(toggle("vistaMirrorPerfFixes", client.vistaMirrorPerfFixes))
                        .option(intSlider("vistaMirrorReflectionDistance", client.vistaMirrorReflectionDistance, 8, 2048, 8))
                        .option(toggle("vistaMirrorFog", client.vistaMirrorFog))
                        .option(intSlider("vistaMirrorFogStart", client.vistaMirrorFogStart, 0, 100, 1))
                        .option(intSlider("vistaMirrorFogEnd", client.vistaMirrorFogEnd, 10, 100, 1))
                        .option(doubleSlider("vistaMirrorUpdateFps", client.vistaMirrorUpdateFps, 1.0, 240.0, 1.0))
                        .option(doubleSlider("vistaMirrorMinUpdateFps", client.vistaMirrorMinUpdateFps, 0.25, 240.0, 0.25))
                        .option(doubleSlider("vistaMirrorIdleUpdateFps", client.vistaMirrorIdleUpdateFps, 0.25, 240.0, 0.25))
                        .option(doubleSlider("vistaMirrorThrottleBudgetMs", client.vistaMirrorThrottleBudgetMs, 0.5, 1000.0, 0.5))
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

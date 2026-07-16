package com.evandev.spicedcider.compat.yacl;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
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
                        .option(toggle("spiderRangedWebAttacks", common.spiderRangedWebAttacks))
                        .option(toggle("removeQuasiConnectivity", common.removeQuasiConnectivity))
                        .option(toggle("skeletonHealthNerf", startup.skeletonHealthNerf))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.compat"))
                        .tooltip(Component.translatable("category.spicedcider.compat.tooltip"))
                        .optionIf(ModList.get().isLoaded("cookscollection"),
                                toggle("cooksCollectionDripstoneFix", startup.cooksCollectionDripstoneFix))
                        .optionIf(ModList.get().isLoaded("environmental"),
                                toggle("wisteriaLeafDensityFix", common.wisteriaLeafDensityFix))
                        .optionIf(ModList.get().isLoaded("blockbox") && ModList.get().isLoaded("everycomp") && ModList.get().isLoaded("moonlight"),
                                toggle("blockBoxWoodVariants", startup.blockBoxWoodVariants))
                        .optionIf(ModList.get().isLoaded("sodium"),
                                toggle("sodiumLightingParityFix", client.sodiumLightingParityFix))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.performance"))
                        .tooltip(Component.translatable("category.spicedcider.performance.tooltip"))
                        .option(toggle("disableRecipeBookTracking", common.disableRecipeBookTracking))
                        .option(toggle("skipRedundantBlockCacheRebuild", common.skipRedundantBlockCacheRebuild))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.client"))
                        .tooltip(Component.translatable("category.spicedcider.client.tooltip"))
                        .option(toggle("randomWorldNaming", client.randomWorldNaming))
                        .option(toggle("customDeathSound", client.customDeathSound))
                        .optionIf(ModList.get().isLoaded("melancholic_hunger"),
                                toggle("hideMelancholicHungerTooltip", client.hideMelancholicHungerTooltip))
                        .option(toggle("cloudFarPlaneFix", client.cloudFarPlaneFix))
                        .option(intSlider("cloudFarPlaneDistance", client.cloudFarPlaneDistance, 0, 32768, 128))
                        .build())
                .categoryIf(ModList.get().isLoaded("vista"), ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.vista"))
                        .tooltip(Component.translatable("category.spicedcider.vista.tooltip"))
                        .option(toggle("vistaMirrorAlwaysConnect", common.vistaMirrorAlwaysConnect))
                        .option(toggle("vistaMirrorPerfFixes", client.vistaMirrorPerfFixes))
                        .option(intSlider("vistaMirrorReflectionDistance", client.vistaMirrorReflectionDistance, 8, 2048, 8))
                        .option(doubleSlider("vistaMirrorUpdateFps", client.vistaMirrorUpdateFps, 1.0, 240.0, 1.0))
                        .option(doubleSlider("vistaMirrorMinUpdateFps", client.vistaMirrorMinUpdateFps, 0.25, 240.0, 0.25))
                        .option(doubleSlider("vistaMirrorIdleUpdateFps", client.vistaMirrorIdleUpdateFps, 0.25, 240.0, 0.25))
                        .option(doubleSlider("vistaMirrorThrottleBudgetMs", client.vistaMirrorThrottleBudgetMs, 0.5, 1000.0, 0.5))
                        .build())
                .build()
                .generateScreen(parent);
    }

    private static Option<Boolean> toggle(String key, ModConfigSpec.BooleanValue value) {
        Consumer<Boolean> setter = value::set;

        return Option.<Boolean>createBuilder()
                .name(Component.translatable("option.spicedcider." + key))
                .description(OptionDescription.of(Component.translatable("option.spicedcider." + key + ".tooltip")))
                .binding(true, value, setter)
                .controller(TickBoxControllerBuilder::create)
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

    private static Option<Double> doubleSlider(String key, ModConfigSpec.DoubleValue value, double min, double max, double step) {
        return Option.<Double>createBuilder()
                .name(Component.translatable("option.spicedcider." + key))
                .description(OptionDescription.of(Component.translatable("option.spicedcider." + key + ".tooltip")))
                .binding(value.getDefault(), value, value::set)
                .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(min, max).step(step))
                .build();
    }
}

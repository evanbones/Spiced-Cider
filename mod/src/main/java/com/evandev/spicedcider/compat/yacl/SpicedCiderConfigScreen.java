package com.evandev.spicedcider.compat.yacl;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
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
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.performance"))
                        .tooltip(Component.translatable("category.spicedcider.performance.tooltip"))
                        .option(toggle("disableRecipeBookTracking", common.disableRecipeBookTracking))
                        .option(toggle("skipRedundantBlockCacheRebuild", common.skipRedundantBlockCacheRebuild))
                        .option(toggle("filterUnusedResourcePackNamespaces", startup.filterUnusedResourcePackNamespaces))
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.client"))
                        .tooltip(Component.translatable("category.spicedcider.client.tooltip"))
                        .option(toggle("randomWorldNaming", client.randomWorldNaming))
                        .option(toggle("customDeathSound", client.customDeathSound))
                        .optionIf(ModList.get().isLoaded("melancholic_hunger"),
                                toggle("hideMelancholicHungerTooltip", client.hideMelancholicHungerTooltip))
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
}

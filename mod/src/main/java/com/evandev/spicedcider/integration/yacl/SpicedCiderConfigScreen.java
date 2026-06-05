package com.evandev.spicedcider.integration.yacl;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SpicedCiderConfigScreen {

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("title.spicedcider.config"))
                .save(() -> {
                    SpicedCiderConfig.COMMON_SPEC.save();
                    SpicedCiderConfig.CLIENT_SPEC.save();
                })
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.client"))
                        .tooltip(Component.translatable("category.spicedcider.client.tooltip"))
                        .option(Option.<String>createBuilder()
                                .name(Component.translatable("option.spicedcider.exportDirectory"))
                                .description(OptionDescription.of(Component.translatable("option.spicedcider.exportDirectory.desc")))
                                .binding(
                                        "resourcepacks/spicedcider_resources/assets",
                                        SpicedCiderConfig.CLIENT.exportDirectory,
                                        SpicedCiderConfig.CLIENT.exportDirectory::set
                                )
                                .controller(StringControllerBuilder::create)
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.spicedcider.structures"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("group.spicedcider.search_settings"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("option.spicedcider.useFastStructureLookup"))
                                        .binding(true, SpicedCiderConfig.COMMON.useFastStructureLookup, SpicedCiderConfig.COMMON.useFastStructureLookup::set)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("option.spicedcider.structureSearchTimeout"))
                                        .binding(50, SpicedCiderConfig.COMMON.structureSearchTimeout, SpicedCiderConfig.COMMON.structureSearchTimeout::set)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 300).step(1))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("option.spicedcider.mapSearchRadius"))
                                        .binding(40, SpicedCiderConfig.COMMON.mapSearchRadius, SpicedCiderConfig.COMMON.mapSearchRadius::set)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 200).step(1))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("option.spicedcider.globalSearchRadius"))
                                        .binding(70, SpicedCiderConfig.COMMON.globalSearchRadius, SpicedCiderConfig.COMMON.globalSearchRadius::set)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 200).step(1))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("option.spicedcider.locateSearchRadius"))
                                        .binding(110, SpicedCiderConfig.COMMON.locateSearchRadius, SpicedCiderConfig.COMMON.locateSearchRadius::set)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 500).step(1))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("group.spicedcider.generation_settings"))
                                .option(Option.<Double>createBuilder()
                                        .name(Component.translatable("option.spicedcider.spacingSeparationModifier"))
                                        .binding(1.0, SpicedCiderConfig.COMMON.spacingSeparationModifier, SpicedCiderConfig.COMMON.spacingSeparationModifier::set)
                                        .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.1, 10.0).step(0.1))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("option.spicedcider.minimumStructureDistanceEnabled"))
                                        .binding(false, SpicedCiderConfig.COMMON.minimumStructureDistanceEnabled, SpicedCiderConfig.COMMON.minimumStructureDistanceEnabled::set)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("option.spicedcider.minimumStructureDistance"))
                                        .binding(32, SpicedCiderConfig.COMMON.minimumStructureDistance, SpicedCiderConfig.COMMON.minimumStructureDistance::set)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(16, 512).step(1))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("option.spicedcider.logDuplicatedSalt"))
                                        .binding(true, SpicedCiderConfig.COMMON.logDuplicatedSalt, SpicedCiderConfig.COMMON.logDuplicatedSalt::set)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }
}
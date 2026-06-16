package com.evandev.spicedcider.integration.yacl;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
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
                .build()
                .generateScreen(parent);
    }
}
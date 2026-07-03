package com.evandev.spicedcider.compat.yacl;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
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
                        .build())
                .build()
                .generateScreen(parent);
    }
}
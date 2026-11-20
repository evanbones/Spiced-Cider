package com.evandev.spicedcider.client.options;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

public class StepUpOption {
    private static OptionInstance<Boolean> option;

    public static OptionInstance<Boolean> get() {
        if (option == null) {
            option = OptionInstance.createBoolean(
                    "option.spicedcider.stepUpEnabled",
                    OptionInstance.cachedConstantTooltip(Component.translatable("option.spicedcider.stepUpEnabled.tooltip")),
                    SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.stepUpEnabled, true),
                    value -> {
                        SpicedCiderConfig.CLIENT.stepUpEnabled.set(value);
                        SpicedCiderConfig.CLIENT.stepUpEnabled.save();
                    });
        }
        option.set(SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.stepUpEnabled, true));
        return option;
    }
}

package com.evandev.spicedcider.compat.sodium;

import com.evandev.spicedcider.client.options.setting.Setting;
import com.evandev.spicedcider.client.options.setting.StepSlider;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.caffeinemc.mods.sodium.client.config.structure.BooleanOption;
import net.caffeinemc.mods.sodium.client.config.structure.EnumOption;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.caffeinemc.mods.sodium.client.config.structure.StatefulOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SodiumSetting<V> implements Setting {

    private final StatefulOption<V> option;
    @Nullable
    private final V packDefault;

    private SodiumSetting(StatefulOption<V> option, @Nullable V packDefault) {
        this.option = option;
        this.packDefault = packDefault;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static Setting of(String id, @Nullable Object packDefault) {
        if (ConfigManager.CONFIG == null) return null;
        Option option = ConfigManager.CONFIG.getOption(ResourceLocation.parse(id));
        if (option instanceof BooleanOption || option instanceof EnumOption<?> || option instanceof IntegerOption) {
            StatefulOption<Object> stateful = (StatefulOption<Object>) option;
            Object sodiumDefault = stateful.getDefaultValue().get(ConfigManager.CONFIG);
            boolean compatible = packDefault != null && sodiumDefault != null && packDefault.getClass() == sodiumDefault.getClass();
            return new SodiumSetting<>(stateful, compatible ? packDefault : null);
        }
        return null;
    }

    private V defaultValue() {
        return packDefault != null ? packDefault : option.getDefaultValue().get(ConfigManager.CONFIG);
    }

    public static void reload() {
        if (ConfigManager.CONFIG == null) return;
        ConfigManager.CONFIG.resetAllOptionsFromBindings();
        ConfigManager.CONFIG.invalidateGlobalRebuildDependents();
    }

    public static void apply() {
        if (ConfigManager.CONFIG != null && ConfigManager.CONFIG.anyOptionChanged()) {
            ConfigManager.CONFIG.applyAllOptions();
        }
    }

    @Override
    public Component name() {
        return option.getName();
    }

    @Override
    public List<FormattedCharSequence> tooltip() {
        Component tooltip = option.getTooltip();
        return tooltip == null ? List.of() : Minecraft.getInstance().font.split(tooltip, 200);
    }

    @Override
    public boolean isDefault() {
        return Objects.equals(option.getValidatedValue(), defaultValue());
    }

    @Override
    public boolean canReset() {
        return true;
    }

    @Override
    public void reset() {
        if (packDefault != null) option.modifyValue(packDefault);
        else option.resetToDefault();
        apply();
    }

    @Override
    public boolean isActive() {
        return option.isEnabled();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public AbstractWidget createWidget(int width) {
        if (option instanceof BooleanOption bool) {
            return CycleButton.onOffBuilder(bool.getValidatedValue())
                    .displayOnlyValue()
                    .create(0, 0, width, 20, name(), (button, value) -> {
                        bool.modifyValue(value);
                        apply();
                    });
        }
        if (option instanceof EnumOption enumOption) {
            return enumButton(enumOption, width);
        }
        IntegerOption integer = (IntegerOption) option;
        var range = integer.getSteppedValidator();
        return new StepSlider(width, range.min(), range.max(), range.step(), integer.getValidatedValue(), integer::formatValue, integer::modifyValue, SodiumSetting::apply);
    }

    private static <E extends Enum<E>> AbstractWidget enumButton(EnumOption<E> option, int width) {
        List<E> values = new ArrayList<>();
        for (E value : option.getEnumClass().getEnumConstants()) {
            if (option.isValueAllowed(value)) values.add(value);
        }
        return CycleButton.<E>builder(option::getElementName)
                .withValues(values)
                .withInitialValue(option.getValidatedValue())
                .displayOnlyValue()
                .create(0, 0, width, 20, option.getName(), (button, value) -> {
                    option.modifyValue(value);
                    apply();
                });
    }
}

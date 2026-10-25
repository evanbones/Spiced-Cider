package com.evandev.spicedcider.compat.controlify;

import com.evandev.spicedcider.client.options.SettingsScreen;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.client.KeyMapping;
import com.evandev.spicedcider.client.options.KeyLayout;
import com.evandev.spicedcider.client.options.entry.ButtonEntry;
import com.evandev.spicedcider.client.options.entry.CategoryEntry;
import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.entry.TextEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.client.options.setting.Setting;
import com.evandev.spicedcider.client.options.setting.StepSlider;
import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.config.settings.profile.ProfileSettings;
import dev.isxander.controlify.controller.ControllerEntity;
import dev.isxander.controlify.controller.input.DeadzoneGroup;
import dev.isxander.controlify.gui.screen.ControlifySettingsScreen;
import dev.isxander.controlify.screenop.ScreenProcessorProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ControlifySettings {

    public static void register() {
        ScreenProcessorProvider.registerProvider(SettingsScreen.class, SettingsScreenProcessor::new);
    }

    public static void save() {
        Controlify.instance().config().markDirty();
        Controlify.instance().config().saveIfDirty();
    }

    public static Entry settingsCategory(SettingsScreen screen) {
        List<Entry> list = new ArrayList<>();
        list.add(new ButtonEntry(Component.translatable("spicedcider.options.controller.setup"), () -> Minecraft.getInstance().setScreen(new ControlifySettingsScreen(screen))));
        ControllerEntity controller = Controlify.instance().getCurrentController().orElse(null);
        if (controller == null) {
            list.add(new TextEntry(Component.translatable("spicedcider.options.controller.none")));
            return category(screen, "controller", list);
        }

        ProfileSettings settings = controller.settings();
        ProfileSettings defaults = controller.defaultSettings();
        var sens = settings.input.sensitivity;
        var sensDefault = defaults.input.sensitivity;
        var generic = settings.generic;
        var genericDefault = defaults.generic;

        list.add(percent(Component.translatable("spicedcider.options.controller.sensitivity_horizontal"), Component.translatable("controlify.gui.horizontal_look_sensitivity.tooltip"), 10, 200, 5,
                () -> sens.hLookSensitivity, v -> sens.hLookSensitivity = v, sensDefault.hLookSensitivity));
        list.add(percent(Component.translatable("spicedcider.options.controller.sensitivity_vertical"), Component.translatable("controlify.gui.vertical_look_sensitivity.tooltip"), 10, 200, 5,
                () -> sens.vLookSensitivity, v -> sens.vLookSensitivity = v, sensDefault.vLookSensitivity));
        list.add(toggle(Component.translatable("controlify.gui.invert_vertical_look"), Component.translatable("controlify.gui.invert_vertical_look.tooltip"),
                () -> sens.vLookInvert, v -> sens.vLookInvert = v, sensDefault.vLookInvert));
        list.add(toggle(Component.translatable("controlify.gui.reduce_aiming_sensitivity"), Component.translatable("controlify.gui.reduce_aiming_sensitivity.tooltip"),
                () -> sens.reduceAimingSensitivity, v -> sens.reduceAimingSensitivity = v, sensDefault.reduceAimingSensitivity));
        list.add(percent(Component.translatable("spicedcider.options.controller.menu_sensitivity"), Component.translatable("controlify.gui.vmouse_sensitivity.tooltip"), 10, 200, 5,
                () -> sens.virtualMouseSensitivity, v -> sens.virtualMouseSensitivity = v, sensDefault.virtualMouseSensitivity));

        controller.input().ifPresent(input -> {
            var inputSens = input.settings().sensitivity;
            var inputSensDefault = input.defaultSettings().sensitivity;
            for (DeadzoneGroup group : input.getDeadzoneGroups().values()) {
                String id = group.name().getPath();
                Component stick = Component.translatable("controlify.deadzone_group." + group.name().getNamespace() + "." + id);
                Component name = switch (id) {
                    case "left_stick" -> Component.translatable("spicedcider.options.controller.deadzone_left");
                    case "right_stick" -> Component.translatable("spicedcider.options.controller.deadzone_right");
                    default -> Component.translatable("controlify.gui.axis_deadzone", stick);
                };
                list.add(percent(name, Component.translatable("controlify.gui.axis_deadzone.tooltip", stick), 0, 100, 2,
                        () -> inputSens.getDeadzone(group.name()), v -> inputSens.putDeadzone(group.name(), v), inputSensDefault.getDeadzone(group.name())));
            }
            for (ButtonSwap swap : ButtonSwap.values()) {
                list.add(new SettingEntry(CycleSetting.toggle(swap.title(), () -> swap.isSwapped(input.getAllBindings()), value -> {
                    if (value != swap.isSwapped(input.getAllBindings())) {
                        swap.apply(input.getAllBindings());
                        save();
                    }
                }).defaultValue(false).tooltip(swap.tooltip())));
            }
        });

        if (controller.rumble().isPresent()) {
            list.add(toggle(Component.translatable("spicedcider.options.controller.vibration"), Component.translatable("spicedcider.options.controller.vibration.tooltip"),
                    () -> settings.rumble.enabled, v -> settings.rumble.enabled = v, defaults.rumble.enabled));
        }
        list.add(toggle(Component.translatable("controlify.gui.show_ingame_guide"), Component.translatable("controlify.gui.show_ingame_guide.tooltip"),
                () -> generic.guide.showIngameGuide, v -> generic.guide.showIngameGuide = v, genericDefault.guide.showIngameGuide));
        list.add(toggle(Component.translatable("controlify.gui.show_screen_guide"), Component.translatable("controlify.gui.show_screen_guide.tooltip"),
                () -> generic.guide.showScreenGuides, v -> generic.guide.showScreenGuides = v, genericDefault.guide.showScreenGuides));

        controller.input().ifPresent(input -> list.add(new ButtonEntry(Component.translatable("controlify.gui.reset_all_binds"), () -> {
            for (InputBinding binding : input.getAllBindings()) binding.setBoundInput(binding.defaultInput());
            save();
        })));
        return category(screen, "controller", list);
    }

    public static void syncMovement(Options options) {
        Controlify.instance().getCurrentController().ifPresent(controller -> {
            var generic = controller.settings().generic;
            boolean changed = generic.toggleSneak != options.toggleCrouch().get()
                    || generic.toggleSprint != options.toggleSprint().get()
                    || generic.autoJump != options.autoJump().get();
            if (!changed) return;
            generic.toggleSneak = options.toggleCrouch().get();
            generic.toggleSprint = options.toggleSprint().get();
            generic.autoJump = options.autoJump().get();
            save();
        });
    }

    private static final List<String> BUTTON_GROUPS = List.of("movement", "gameplay", "hotbar", "interface", "menus", "camera", "voice_chat", "emotes", KeyLayout.OTHER);

    private static final String MODDED_NAMESPACE = "fabric-key-binding-api-v1";

    private static String nativeGroup(InputBinding binding) {
        String category = binding.category().getContents() instanceof TranslatableContents contents ? contents.getKey() : "";
        return switch (category) {
            case "key.categories.movement" -> "movement";
            case "key.categories.gameplay" -> "gameplay";
            case "key.categories.inventory", "key.categories.creative" -> "hotbar";
            case "controlify.binding_category.gui", "controlify.gui.radial_menu" -> "interface";
            case "controlify.binding_category.vmouse" -> "menus";
            case "controlify.binding_category.debug" -> null;
            default -> KeyLayout.OTHER;
        };
    }

    private static String moddedGroup(InputBinding binding, Map<String, String> keyNames) {
        String keyName = keyNames.get(binding.id().getPath());
        if (keyName == null) return KeyLayout.OTHER;
        String group = KeyLayout.groupOf(keyName);
        if (group == null) return null;
        return BUTTON_GROUPS.contains(group) ? group : KeyLayout.OTHER;
    }

    public static List<Entry> bindingCategories(SettingsScreen screen) {
        List<Entry> entries = new ArrayList<>();
        Controlify.instance().getCurrentController().ifPresent(controller -> controller.input().ifPresent(input -> {
            Map<String, String> keyNames = new HashMap<>();
            for (KeyMapping mapping : Minecraft.getInstance().options.keyMappings) {
                keyNames.put(mapping.getName().toLowerCase().replaceAll("[^a-z0-9/._-]", "_").trim(), mapping.getName());
            }
            Collection<InputBinding> bindings = input.getAllBindings();
            Map<String, List<Entry>> groups = new LinkedHashMap<>();
            for (String group : BUTTON_GROUPS) groups.put(group, new ArrayList<>());
            for (InputBinding binding : bindings) {
                String group = binding.id().getNamespace().equals(MODDED_NAMESPACE) ? moddedGroup(binding, keyNames) : nativeGroup(binding);
                if (group != null) groups.get(group).add(new ControllerBindEntry(screen, controller, binding, bindings));
            }
            groups.forEach((group, list) -> {
                if (!list.isEmpty()) {
                    entries.add(new CategoryEntry(Component.translatable("spicedcider.options.category.buttons." + group), list, screen.collapsed(), true));
                }
            });
        }));
        return entries;
    }

    private static Entry category(SettingsScreen screen, String key, List<Entry> children) {
        return new CategoryEntry(Component.translatable("spicedcider.options.category." + key), children, screen.collapsed());
    }

    private static Entry toggle(Component name, Component tooltip, Supplier<Boolean> getter, Consumer<Boolean> setter, boolean defaultValue) {
        return new SettingEntry(CycleSetting.toggle(name, getter, value -> {
            setter.accept(value);
            save();
        }).defaultValue(defaultValue).tooltip(tooltip));
    }

    private static Entry percent(Component name, Component tooltip, int min, int max, int step, Supplier<Float> getter, Consumer<Float> setter, float defaultValue) {
        return new SettingEntry(new PercentSetting(name, tooltip, min, max, step, getter, setter, defaultValue));
    }

    private record PercentSetting(Component name, Component tooltipText, int min, int max, int step, Supplier<Float> getter, Consumer<Float> setter, float defaultValue) implements Setting {

        private static int toPercent(float value) {
            return Math.round(value * 100);
        }

        @Override
        public List<FormattedCharSequence> tooltip() {
            return Minecraft.getInstance().font.split(tooltipText, 200);
        }

        @Override
        public boolean isDefault() {
            return toPercent(getter.get()) == toPercent(defaultValue);
        }

        @Override
        public boolean canReset() {
            return true;
        }

        @Override
        public void reset() {
            setter.accept(defaultValue);
            save();
        }

        @Override
        public AbstractWidget createWidget(int width) {
            return new StepSlider(width, min, max, step, toPercent(getter.get()),
                    value -> Component.translatable("spicedcider.options.value.percent", value),
                    value -> setter.accept(value / 100.0F),
                    ControlifySettings::save);
        }
    }
}

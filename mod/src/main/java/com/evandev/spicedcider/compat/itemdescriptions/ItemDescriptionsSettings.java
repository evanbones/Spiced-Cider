package com.evandev.spicedcider.compat.itemdescriptions;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.options.entry.Entry;
import com.evandev.spicedcider.client.options.entry.SettingEntry;
import com.evandev.spicedcider.client.options.setting.CycleSetting;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public final class ItemDescriptionsSettings {

    public enum Mode {
        OFF(null),
        ALWAYS(null),
        HOLD_SHIFT("displayWhenShiftIsHeld"),
        HOLD_CTRL("displayWhenCtrlIsHeld"),
        HOLD_ALT("displayWhenAltIsHeld");

        private final String keyField;

        Mode(String keyField) {
            this.keyField = keyField;
        }

        Component display() {
            return Component.translatable("spicedcider.options.item_description_tooltips." + name().toLowerCase(Locale.ROOT));
        }
    }

    private static final String TRACKED_VALUE = "folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue";
    private static final String[] KEY_FIELDS = {"displayWhenShiftIsHeld", "displayWhenCtrlIsHeld", "displayWhenAltIsHeld"};

    public static Entry mode() {
        Object config = config();
        if (config == null) return null;
        return new SettingEntry(new CycleSetting<>(
                Component.translatable("spicedcider.options.item_description_tooltips"),
                () -> List.of(Mode.values()),
                () -> read(config),
                mode -> write(config, mode),
                Mode::display
        ).defaultValue(Mode.OFF).tooltip(Component.translatable("spicedcider.options.item_description_tooltips.tooltip")));
    }

    private static Object config() {
        try {
            return Class.forName("cc.cassian.item_descriptions.client.ModClient").getField("CONFIG").get(null);
        } catch (ReflectiveOperationException | LinkageError e) {
            SpicedCider.LOGGER.warn("Item Descriptions config unavailable", e);
            return null;
        }
    }

    private static Mode read(Object config) {
        try {
            if (get(config, "displayAlways")) return Mode.ALWAYS;
            Object keybinds = field(config, "keybinds");
            for (Mode mode : Mode.values()) {
                if (mode.keyField != null && get(keybinds, mode.keyField)) return mode;
            }
        } catch (ReflectiveOperationException e) {
            SpicedCider.LOGGER.warn("Couldn't read Item Descriptions config", e);
        }
        return Mode.OFF;
    }

    private static void write(Object config, Mode mode) {
        try {
            set(config, "itemDescriptions", true);
            set(config, "displayAlways", mode == Mode.ALWAYS);
            Object keybinds = field(config, "keybinds");
            for (String key : KEY_FIELDS) {
                set(keybinds, key, key.equals(mode.keyField));
            }
            set(keybinds, "invert", false);
            config.getClass().getMethod("save").invoke(config);
            Class.forName("cc.cassian.item_descriptions.client.helpers.ModStyle").getMethod("updateStyles").invoke(null);
        } catch (ReflectiveOperationException e) {
            SpicedCider.LOGGER.warn("Couldn't save Item Descriptions config", e);
        }
    }

    private static Object field(Object owner, String name) throws ReflectiveOperationException {
        Field field = owner.getClass().getField(name);
        return field.get(owner);
    }

    private static boolean get(Object owner, String name) throws ReflectiveOperationException {
        Object tracked = field(owner, name);
        return Boolean.TRUE.equals(trackedValue(tracked).getMethod("value").invoke(tracked));
    }

    private static void set(Object owner, String name, boolean v) throws ReflectiveOperationException {
        Object tracked = field(owner, name);
        trackedValue(tracked).getMethod("setValue", Object.class).invoke(tracked, v);
    }

    private static Class<?> trackedValue(Object tracked) throws ClassNotFoundException {
        return Class.forName(TRACKED_VALUE, false, tracked.getClass().getClassLoader());
    }
}

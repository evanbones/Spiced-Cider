package com.evandev.spicedcider.compat.lambdynlights;

import com.evandev.spicedcider.client.options.setting.CycleSetting;
import com.evandev.spicedcider.client.options.setting.Setting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public final class DynamicLightsSetting {

    @Nullable
    public static Setting create() {
        try {
            Class<?> modClass = Class.forName("dev.lambdaurora.lambdynlights.LambDynLights");
            Class<?> configClass = Class.forName("dev.lambdaurora.lambdynlights.DynamicLightsConfig");
            Class<?> modeClass = Class.forName("dev.lambdaurora.lambdynlights.DynamicLightsMode");
            Object config = modClass.getField("config").get(modClass.getMethod("get").invoke(null));
            Method getMode = configClass.getMethod("getDynamicLightsMode");
            Method setMode = configClass.getMethod("setDynamicLightsMode", modeClass);
            Method save = configClass.getMethod("save");
            Method name = modeClass.getMethod("getTranslatedText");
            List<Object> modes = Arrays.asList(modeClass.getEnumConstants());
            Object fancy = modes.stream().filter(mode -> ((Enum<?>) mode).name().equals("FANCY")).findFirst().orElse(null);

            return new CycleSetting<>(
                    Component.translatable("spicedcider.options.dynamic_lights"),
                    () -> modes,
                    () -> invoke(getMode, config),
                    mode -> {
                        invoke(setMode, config, mode);
                        invoke(save, config);
                    },
                    mode -> (Component) invoke(name, mode)
            ).defaultValue(fancy).tooltip(Component.translatable("spicedcider.options.dynamic_lights.tooltip"));
        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }

    private static Object invoke(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}

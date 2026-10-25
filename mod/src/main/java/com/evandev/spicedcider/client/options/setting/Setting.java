package com.evandev.spicedcider.client.options.setting;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public interface Setting {

    Component name();

    default List<FormattedCharSequence> tooltip() {
        return List.of();
    }

    boolean isDefault();

    boolean canReset();

    void reset();

    default boolean isActive() {
        return true;
    }

    AbstractWidget createWidget(int width);

    default void beforeRender(AbstractWidget widget) {
    }

    default void afterRelease() {
    }

    default Setting named(Component name) {
        Setting base = this;
        return new Setting() {
            @Override
            public Component name() {
                return name;
            }

            @Override
            public List<FormattedCharSequence> tooltip() {
                return base.tooltip();
            }

            @Override
            public boolean isDefault() {
                return base.isDefault();
            }

            @Override
            public boolean canReset() {
                return base.canReset();
            }

            @Override
            public void reset() {
                base.reset();
            }

            @Override
            public boolean isActive() {
                return base.isActive();
            }

            @Override
            public AbstractWidget createWidget(int width) {
                return base.createWidget(width);
            }

            @Override
            public void beforeRender(AbstractWidget widget) {
                base.beforeRender(widget);
            }

            @Override
            public void afterRelease() {
                base.afterRelease();
            }
        };
    }
}

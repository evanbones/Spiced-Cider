package com.evandev.spicedcider.content.block;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TubewormPart implements StringRepresentable {
    TOP("top"),
    MIDDLE("middle");

    private final String name;

    TubewormPart(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
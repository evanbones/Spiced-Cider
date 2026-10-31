package com.evandev.spicedcider.compat.etcetera;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ChiseledVariantsClient {

    @Nullable
    static Level level() {
        return Minecraft.getInstance().level;
    }
}

package com.evandev.spicedcider.mixin.minecraft.client;

import net.minecraft.client.gui.screens.ProgressScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProgressScreen.class)
public interface ProgressScreenAccessor {

    @Accessor("clearScreenAfterStop")
    boolean spicedcider$clearScreenAfterStop();

    @Accessor("stop")
    boolean spicedcider$stop();
}

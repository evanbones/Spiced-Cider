package com.evandev.spicedcider.mixin.minecraft.client;

import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelLoadingScreen.class)
public interface LevelLoadingScreenAccessor {

    @Accessor("progressListener")
    StoringChunkProgressListener spicedcider$getProgressListener();
}

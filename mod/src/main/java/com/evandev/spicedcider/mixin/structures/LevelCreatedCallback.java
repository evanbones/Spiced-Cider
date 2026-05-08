package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.structures.SpicedCiderStructureCompat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class LevelCreatedCallback {
    @Inject(method = "createLevels", at = @At("TAIL"))
    private void afterLevelCreation(ChunkProgressListener p_129816_, CallbackInfo ci) {
        SpicedCiderStructureCompat.onServerStart((MinecraftServer) (Object) this);
    }
}
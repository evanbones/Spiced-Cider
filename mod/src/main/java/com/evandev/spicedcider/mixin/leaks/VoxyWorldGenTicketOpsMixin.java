package com.evandev.spicedcider.mixin.leaks;

import com.ethan.voxyworldgenv2.core.ChunkGenerationManager;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.atomic.AtomicBoolean;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = ChunkGenerationManager.class, remap = false)
public class VoxyWorldGenTicketOpsMixin {
    @Shadow
    @Final
    private AtomicBoolean running;

    @WrapMethod(method = "queueTicketAdd")
    private void spicedcider$dropAddAfterShutdown(ServerLevel level, ChunkPos pos, Operation<Void> original) {
        if (running.get()) original.call(level, pos);
    }

    @WrapMethod(method = "queueTicketRemove")
    private void spicedcider$dropRemoveAfterShutdown(ServerLevel level, ChunkPos pos, Operation<Void> original) {
        if (running.get()) original.call(level, pos);
    }
}

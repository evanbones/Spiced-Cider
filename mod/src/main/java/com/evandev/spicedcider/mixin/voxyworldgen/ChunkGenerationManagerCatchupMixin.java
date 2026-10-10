package com.evandev.spicedcider.mixin.voxyworldgen;

import com.evandev.spicedcider.compat.voxyworldgen.VoxyWorldGenCatchup;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.ethan.voxyworldgenv2.core.ChunkGenerationManager;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = ChunkGenerationManager.class, remap = false)
public class ChunkGenerationManagerCatchupMixin {

    @Shadow
    @Final
    private Map<ResourceKey<Level>, ?> dimensionStates;

    @Shadow
    private MinecraftServer server;

    @Inject(method = "runCatchup", at = @At("HEAD"), cancellable = true)
    private void spicedcider$nonBlockingCatchup(List<ServerPlayer> players, CallbackInfo ci) {
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.voxyWorldGenSafeCatchup, true)) return;
        ci.cancel();
        VoxyWorldGenCatchup.runCatchup(server, dimensionStates, players);
    }

    @Inject(method = "initialize", at = @At("HEAD"))
    private void spicedcider$resetOnInitialize(MinecraftServer server, CallbackInfo ci) {
        VoxyWorldGenCatchup.reset();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void spicedcider$resetOnShutdown(CallbackInfo ci) {
        VoxyWorldGenCatchup.reset();
    }
}

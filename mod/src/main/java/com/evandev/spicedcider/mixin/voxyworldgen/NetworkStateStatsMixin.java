package com.evandev.spicedcider.mixin.voxyworldgen;

import com.evandev.spicedcider.compat.voxyworldgen.VoxyWorldGenStats;
import com.ethan.voxyworldgenv2.network.NetworkState;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicLong;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = NetworkState.class, remap = false)
public class NetworkStateStatsMixin {

    @Shadow
    @Final
    private static AtomicLong chunksReceived;

    @Inject(method = "setServerConnected", at = @At("HEAD"))
    private static void spicedcider$saveLodStats(boolean connected, CallbackInfo ci) {
        if (!connected && VoxyWorldGenStats.enabled()) {
            VoxyWorldGenStats.save(chunksReceived.get());
        }
    }

    @Inject(method = "setServerConnected", at = @At("RETURN"))
    private static void spicedcider$loadLodStats(boolean connected, CallbackInfo ci) {
        if (connected && VoxyWorldGenStats.enabled()) {
            chunksReceived.set(VoxyWorldGenStats.load());
        }
    }
}

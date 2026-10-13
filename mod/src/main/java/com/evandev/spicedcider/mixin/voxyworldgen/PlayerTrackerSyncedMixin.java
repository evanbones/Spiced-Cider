package com.evandev.spicedcider.mixin.voxyworldgen;

import com.evandev.spicedcider.compat.voxyworldgen.VoxyWorldGenCatchup;
import com.ethan.voxyworldgenv2.core.PlayerTracker;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = PlayerTracker.class, remap = false)
public class PlayerTrackerSyncedMixin {

    @Inject(method = "clearSynced", at = @At("HEAD"))
    private void spicedcider$persistBeforeClear(UUID uuid, CallbackInfo ci) {
        VoxyWorldGenCatchup.detach(uuid);
    }

    @Inject(method = "removePlayer", at = @At("HEAD"))
    private void spicedcider$persistOnLeave(ServerPlayer player, CallbackInfo ci) {
        VoxyWorldGenCatchup.detach(player.getUUID());
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void spicedcider$persistOnClear(CallbackInfo ci) {
        VoxyWorldGenCatchup.detachAll();
    }
}

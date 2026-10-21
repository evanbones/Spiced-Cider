package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.entities.balloon.BalloonTethers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerBalloonMixin {
    @Shadow
    public ServerPlayer player;
    @Shadow
    private int aboveGroundTickCount;
    @Shadow
    private int aboveGroundVehicleTickCount;

    @Inject(method = "tick", at = @At("HEAD"))
    private void balloonsAreNotFlying(CallbackInfo ci) {
        if (BalloonTethers.count(this.player) > 0) {
            this.aboveGroundTickCount = 0;
        }
        if (BalloonTethers.count(this.player.getRootVehicle()) > 0) {
            this.aboveGroundVehicleTickCount = 0;
        }
    }
}

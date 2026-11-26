package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public abstract class ServerLevelLocalBossSoundsMixin {
    private static final double SPICEDCIDER$RANGE = 96.0;

    @WrapOperation(method = "globalLevelEvent", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"))
    private void spicedcider$localBossSounds(PlayerList players, Packet<?> packet, Operation<Void> original, int id, BlockPos pos, int data) {
        if ((id == 1023 || id == 1028 || id == 1038) && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.localizedBossSounds, false)) {
            ServerLevel level = (ServerLevel) (Object) this;
            players.broadcast(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SPICEDCIDER$RANGE, level.dimension(), packet);
            return;
        }
        original.call(players, packet);
    }
}

package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerAdvancements.class)
public abstract class PlayerAdvancementsPrivateMessageMixin {

    @Shadow
    private ServerPlayer player;

    @WrapOperation(method = "lambda$award$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void spicedcider$announceToEarnerOnly(PlayerList playerList, Component message, boolean overlay, Operation<Void> original) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.privateAdvancementMessages, false)) {
            player.sendSystemMessage(message, overlay);
        } else {
            original.call(playerList, message, overlay);
        }
    }
}

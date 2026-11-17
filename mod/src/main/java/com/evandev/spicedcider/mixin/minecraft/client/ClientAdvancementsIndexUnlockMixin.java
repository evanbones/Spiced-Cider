package com.evandev.spicedcider.mixin.minecraft.client;

import com.evandev.spicedcider.compat.emidiscovery.IndexUnlocks;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancements.class)
public class ClientAdvancementsIndexUnlockMixin {

    @Inject(method = "update", at = @At("TAIL"))
    private void spicedcider$applyIndexUnlocks(ClientboundUpdateAdvancementsPacket packet, CallbackInfo ci) {
        IndexUnlocks.onAdvancementsUpdated(packet);
    }
}

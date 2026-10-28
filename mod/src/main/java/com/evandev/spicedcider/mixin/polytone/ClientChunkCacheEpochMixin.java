package com.evandev.spicedcider.mixin.polytone;

import com.evandev.spicedcider.compat.polytone.ClientChunkEpoch;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.multiplayer.ClientChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("polytone")
@Mixin(ClientChunkCache.class)
public class ClientChunkCacheEpochMixin {

    @Inject(method = {"drop", "updateViewCenter", "updateViewRadius"}, at = @At("HEAD"))
    private void spicedcider$bumpEpoch(CallbackInfo ci) {
        ClientChunkEpoch.bump();
    }

    @Inject(method = "replaceWithPacketData", at = @At("HEAD"))
    private void spicedcider$bumpEpochOnLoad(CallbackInfoReturnable<?> cir) {
        ClientChunkEpoch.bump();
    }
}

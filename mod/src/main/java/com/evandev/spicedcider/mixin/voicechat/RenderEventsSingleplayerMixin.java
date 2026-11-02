package com.evandev.spicedcider.mixin.voicechat;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("voicechat")
@Mixin(targets = "de.maxhenkel.voicechat.voice.client.RenderEvents", remap = false)
public class RenderEventsSingleplayerMixin {

    @Inject(method = "onRenderHUD", at = @At("HEAD"), cancellable = true)
    private void spicedcider$hideInSingleplayer(CallbackInfo ci) {
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.voiceChatHideSingleplayerHud, false)) return;
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null && !server.isPublished()) ci.cancel();
    }
}

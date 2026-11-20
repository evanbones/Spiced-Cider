package com.evandev.spicedcider.mixin.personality;

import com.evandev.spicedcider.compat.personality.PersonalitySitting;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.personality.common.network.SitPayload;
import com.teamabnormals.personality.common.network.handler.ServerPayloadHandler;
import com.teamabnormals.personality.core.other.PersonalityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("personality")
@Mixin(value = ServerPayloadHandler.class, remap = false)
public class SitPayloadTrackingMixin {
    @Inject(method = "handleSit", at = @At("RETURN"))
    private static void spicedcider$trackSitting(SitPayload payload, IPayloadContext context, CallbackInfo ci) {
        if (context.player() instanceof ServerPlayer player) {
            PersonalitySitting.setSitting(player, payload.isSitting() && PersonalityEvents.testSit(player));
        }
    }
}

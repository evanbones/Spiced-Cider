package com.evandev.spicedcider.mixin.fieldguide;

import com.evandev.spicedcider.registry.ModCriteria;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("fieldguide")
@Mixin(targets = "com.evandev.fieldguide.network.ScanUnlockPacket", remap = false)
public class ScanUnlockAdvancementMixin {

    @Inject(method = "handleServer", at = @At(value = "INVOKE", target = "Lcom/evandev/fieldguide/server/progress/PlayerFieldGuideProgress;tryUnlock(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;Lcom/evandev/fieldguide/api/EntryUnlockData$UnlockTrigger;)V"))
    private void spicedcider$scanAdvancement(ServerPlayer player, CallbackInfo ci) {
        ModCriteria.FIELD_GUIDE_SCAN.get().trigger(player);
    }
}

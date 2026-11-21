package com.evandev.spicedcider.mixin.personality;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.personality.core.other.PersonalityClientEvents;
import com.teamabnormals.personality.core.other.PersonalityKeyBindings;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("personality")
@Mixin(value = PersonalityClientEvents.class, remap = false)
public class CrawlKeyDisableMixin {
    @WrapOperation(method = "onClientTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
    private static boolean spicedcider$noCrawl(KeyMapping key, Operation<Boolean> original) {
        if (key == PersonalityKeyBindings.CRAWL && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.personalityNoCrawl, false)) {
            return false;
        }
        return original.call(key);
    }
}

package com.evandev.spicedcider.mixin.personality;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.personality.core.other.PersonalityKeyBindings;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("personality")
@Mixin(value = PersonalityKeyBindings.class, remap = false)
public class CrawlKeyRegistrationMixin {
    @WrapOperation(method = "registerKeyBindings", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/event/RegisterKeyMappingsEvent;register(Lnet/minecraft/client/KeyMapping;)V"))
    private static void spicedcider$hideCrawlKey(RegisterKeyMappingsEvent event, KeyMapping key, Operation<Void> original) {
        if (key == PersonalityKeyBindings.CRAWL && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.personalityNoCrawl, false)) {
            return;
        }
        original.call(event, key);
    }
}

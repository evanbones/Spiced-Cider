package com.evandev.spicedcider.mixin.personality;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.teamabnormals.personality.common.network.handler.ServerPayloadHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("personality")
@Mixin(value = ServerPayloadHandler.class, remap = false)
public class CrawlPayloadDisableMixin {
    @ModifyExpressionValue(method = "handleCrawl", at = @At(value = "INVOKE", target = "Lcom/teamabnormals/personality/common/network/CrawlPayload;isCrawling()Z"))
    private static boolean spicedcider$noCrawl(boolean crawling) {
        return crawling && !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.personalityNoCrawl, false);
    }
}

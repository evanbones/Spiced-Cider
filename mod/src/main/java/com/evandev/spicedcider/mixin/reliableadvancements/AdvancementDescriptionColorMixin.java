package com.evandev.spicedcider.mixin.reliableadvancements;

import com.evandev.reliable_advancements.gui.EnhancedAdvancementWidget;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("reliable_advancements")
@Mixin(EnhancedAdvancementWidget.class)
public class AdvancementDescriptionColorMixin {

    @WrapOperation(method = "refreshHover", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/AdvancementType;getChatColor()Lnet/minecraft/ChatFormatting;"))
    private ChatFormatting spicedcider$whiteDescription(AdvancementType type, Operation<ChatFormatting> original) {
        return ChatFormatting.WHITE;
    }
}

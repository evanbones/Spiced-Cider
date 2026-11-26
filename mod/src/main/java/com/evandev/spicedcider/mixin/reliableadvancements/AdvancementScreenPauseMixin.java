package com.evandev.spicedcider.mixin.reliableadvancements;

import com.evandev.reliable_advancements.gui.screens.EnhancedAdvancementsScreen;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("reliable_advancements")
@Mixin(EnhancedAdvancementsScreen.class)
public class AdvancementScreenPauseMixin {

    @ModifyReturnValue(method = "isPauseScreen", at = @At("RETURN"))
    private boolean spicedcider$pauseUnlessEditing(boolean pause) {
        return !EnhancedAdvancementsScreen.canEdit();
    }
}

package com.evandev.spicedcider.mixin.jade;

import com.evandev.spicedcider.compat.cavernsandchasms.MonocleJade;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.overlay.WailaTickHandler;

@IfModLoaded("jade")
@Mixin(value = WailaTickHandler.class, remap = false)
public class JadeSpyglassMonocleMixin {

    @WrapOperation(method = "tickClient", at = @At(value = "INVOKE", target = "Lsnownee/jade/api/config/IWailaConfig$IConfigGeneral;shouldDisplayTooltip()Z"))
    private boolean spicedcider$requireSpyglassOrMonocle(IWailaConfig.IConfigGeneral general, Operation<Boolean> original) {
        if (!original.call(general)) return false;
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.jadeNeedsSpyglassOrMonocle, false)) return true;
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && MonocleJade.canSeeTooltip(player);
    }
}

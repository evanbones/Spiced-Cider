package com.evandev.spicedcider.mixin.jade;

import com.evandev.spicedcider.compat.cavernsandchasms.MonocleJade;
import com.evandev.spicedcider.compat.jade.JadeScanReach;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.overlay.RayTracing;

@IfModLoaded("jade")
@Mixin(value = RayTracing.class, remap = false)
public class RayTracingSpyglassReachMixin {

    @WrapOperation(method = "fire", at = @At(value = "INVOKE", target = "Lsnownee/jade/api/config/IWailaConfig$IConfigGeneral;getExtendedReach()F"))
    private float spicedcider$spyglassReach(IWailaConfig.IConfigGeneral general, Operation<Float> original) {
        float reach = original.call(general);
        if (!SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.jadeNeedsSpyglassOrMonocle, false)) return reach;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || !MonocleJade.canSeeTooltip(player)) return reach;
        double extra = JadeScanReach.scanDistance(minecraft) - player.blockInteractionRange();
        return Math.max(reach, (float) extra);
    }
}

package com.evandev.spicedcider.mixin.upgradeaquatic.client;

import com.evandev.spicedcider.mixin.upgradeaquatic.ThrasherAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("upgrade_aquatic")
@Mixin(Gui.class)
public class ThrasherVehicleHealthMixin {

    @ModifyReturnValue(method = "getPlayerVehicleWithHealth", at = @At("RETURN"))
    private LivingEntity spicedcider$hideThrasherHealth(LivingEntity vehicle) {
        return vehicle instanceof ThrasherAccessor ? null : vehicle;
    }
}

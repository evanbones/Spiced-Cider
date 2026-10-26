package com.evandev.spicedcider.mixin.reliablebackpacks;

import com.evandev.reliable_backpacks.common.events.BackpackPickupEvents;
import com.evandev.reliable_backpacks.platform.services.IPlatformHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("reliable_backpacks")
@Mixin(value = BackpackPickupEvents.class, remap = false)
public class BackpackPickupEventsMixin {
    @WrapOperation(
            method = "onRightClickBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/evandev/reliable_backpacks/platform/services/IPlatformHelper;isBackpackEquipped(Lnet/minecraft/world/entity/LivingEntity;)Z"
            )
    )
    private static boolean spicedcider$noPlacementFromBack(IPlatformHelper platform, LivingEntity entity, Operation<Boolean> original) {
        return false;
    }
}

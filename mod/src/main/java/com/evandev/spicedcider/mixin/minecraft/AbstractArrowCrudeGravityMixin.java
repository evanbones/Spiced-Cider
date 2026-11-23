package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.item.CrudeBowItem;
import com.evandev.spicedcider.registry.ModAttachments;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowCrudeGravityMixin {

    @ModifyReturnValue(method = "getDefaultGravity", at = @At("RETURN"))
    private double spicedcider$crudeArrowGravity(double original) {
        return ((AbstractArrow) (Object) this).hasData(ModAttachments.CRUDE_ARROW) ? original * CrudeBowItem.GRAVITY_MULTIPLIER : original;
    }
}

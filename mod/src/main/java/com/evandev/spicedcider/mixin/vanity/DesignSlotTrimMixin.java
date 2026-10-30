package com.evandev.spicedcider.mixin.vanity;

import com.evandev.spicedcider.content.smithing.TrimStyling;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.vanity.common.menu.container.DesignSlot;

@IfModLoaded("vanity")
@Mixin(value = DesignSlot.class, remap = false)
public class DesignSlotTrimMixin {

    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void spicedcider$acceptTrimTemplates(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && TrimStyling.isTemplate(stack)) {
            cir.setReturnValue(true);
        }
    }
}

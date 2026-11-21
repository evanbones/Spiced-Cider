package com.evandev.spicedcider.mixin.modestmining;

import com.baisylia.modestmining.item.custom.weapons.JavelinItem;
import com.evandev.spicedcider.util.RemovedRepairItems;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("modestmining")
@Mixin(JavelinItem.class)
public class JavelinItemRemovedRepairMixin {

    @ModifyReturnValue(method = "isValidRepairItem", at = @At("RETURN"))
    private boolean spicedcider$acceptReplacementRepairItem(boolean original, ItemStack toRepair, ItemStack repair) {
        return original || RemovedRepairItems.accepts(((JavelinItem) (Object) this).getTier().getRepairIngredient(), repair);
    }
}

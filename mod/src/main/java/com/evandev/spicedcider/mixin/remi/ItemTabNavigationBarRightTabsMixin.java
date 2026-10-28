package com.evandev.spicedcider.mixin.remi;

import com.evandev.remi.feature.creativemodetab.gui.itemtab.ItemTabNavigationBar;
import com.evandev.spicedcider.compat.remi.RemiRightTabs;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("remi")
@Mixin(value = ItemTabNavigationBar.class, remap = false)
public class ItemTabNavigationBarRightTabsMixin {

    @ModifyExpressionValue(method = "setTabs", at = @At(value = "FIELD", target = "Lcom/evandev/remi/feature/creativemodetab/gui/itemtab/ItemTabNavigationBar;isRightSide:Z"))
    private boolean spicedcider$rightSideStyle(boolean rightSide) {
        return rightSide || RemiRightTabs.active();
    }
}

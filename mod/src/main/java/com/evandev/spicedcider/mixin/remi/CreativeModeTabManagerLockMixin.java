package com.evandev.spicedcider.mixin.remi;

import com.evandev.remi.feature.creativemodetab.CreativeModeTabManager;
import com.evandev.spicedcider.compat.emidiscovery.IndexUnlocks;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@IfModLoaded("remi")
@Mixin(value = CreativeModeTabManager.class, remap = false)
public class CreativeModeTabManagerLockMixin {

    @ModifyReturnValue(method = "getVisibleCreativeModeTabs", at = @At("RETURN"))
    private static List<CreativeModeTab> spicedcider$lockTabs(List<CreativeModeTab> original) {
        return IndexUnlocks.tabsLocked() ? new ArrayList<>() : original;
    }
}

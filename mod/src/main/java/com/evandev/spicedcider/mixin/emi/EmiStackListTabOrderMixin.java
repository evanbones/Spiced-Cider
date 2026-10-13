package com.evandev.spicedcider.mixin.emi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@IfModLoaded("emi")
@Mixin(targets = "dev.emi.emi.registry.EmiStackList", remap = false)
public class EmiStackListTabOrderMixin {

    @WrapOperation(
            method = "reload",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTabs;allTabs()Ljava/util/List;")
    )
    private static List<CreativeModeTab> spicedcider$visibleTabsFirst(Operation<List<CreativeModeTab>> original) {
        List<CreativeModeTab> all = original.call();
        Set<CreativeModeTab> ordered = new LinkedHashSet<>(CreativeModeTabs.tabs());
        ordered.addAll(all);
        return List.copyOf(ordered);
    }
}

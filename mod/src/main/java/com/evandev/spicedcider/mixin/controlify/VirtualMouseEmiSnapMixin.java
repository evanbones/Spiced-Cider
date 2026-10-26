package com.evandev.spicedcider.mixin.controlify;

import com.evandev.spicedcider.compat.emi.EmiSnapPoints;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.isxander.controlify.api.vmousesnapping.SnapPoint;
import dev.isxander.controlify.virtualmouse.VirtualMouseHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashSet;
import java.util.Set;

@IfModLoaded("controlify")
@IfModLoaded("emi")
@Mixin(value = VirtualMouseHandler.class, remap = false)
public class VirtualMouseEmiSnapMixin {

    @ModifyReturnValue(method = "collectSnapPoints", at = @At("RETURN"))
    private Set<SnapPoint> spicedcider$addEmiSnapPoints(Set<SnapPoint> original) {
        if (!(Minecraft.getInstance().screen instanceof AbstractContainerScreen<?>)) return original;
        Set<SnapPoint> points = new HashSet<>(original);
        EmiSnapPoints.collect((x, y, range) -> points.add(new SnapPoint(x, y, range)));
        return points;
    }
}

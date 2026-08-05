package com.evandev.spicedcider.mixin.blockgrid;

import com.evandev.spicedcider.blockgrid.SurfaceSnapping;
import com.evandev.spicedcider.blockgrid.storage.SurfaceOffsetHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HangingEntityItem.class)
public class HangingEntityPlacementMixin {
    @WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/HangingEntity;survives()Z"))
    private boolean spicedcider$applySurfaceMount(HangingEntity entity, Operation<Boolean> original, @Local(argsOnly = true) UseOnContext context) {
        if (entity instanceof SurfaceOffsetHolder holder) {
            holder.spicedcider$setSurfaceOffset(SurfaceSnapping.mountOffsetFor(context));
        }
        return original.call(entity);
    }
}

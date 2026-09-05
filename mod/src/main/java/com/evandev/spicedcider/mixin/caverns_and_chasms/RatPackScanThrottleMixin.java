package com.evandev.spicedcider.mixin.caverns_and_chasms;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.caverns_and_chasms.common.entity.animal.rat.Rat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Predicate;

@Mixin(Rat.class)
public abstract class RatPackScanThrottleMixin {

    @Shadow
    private List<Rat> pack;

    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"
            )
    )
    private List<Rat> spicedcider$throttlePackScan(Level level, Class<Rat> type, AABB area, Predicate<? super Rat> filter, Operation<List<Rat>> original) {
        Rat self = (Rat) (Object) this;
        if (self.tickCount % 20 != 0) {
            return this.pack;
        }
        return original.call(level, type, area, filter);
    }
}

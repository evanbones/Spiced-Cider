package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerSleepingStillMixin {

    @WrapMethod(method = "travel")
    private void spicedcider$stayInBed(Vec3 travelVector, Operation<Void> original) {
        Player player = (Player) (Object) this;
        if (player.isSleeping()) {
            player.setDeltaMovement(Vec3.ZERO);
            return;
        }
        original.call(travelVector);
    }
}

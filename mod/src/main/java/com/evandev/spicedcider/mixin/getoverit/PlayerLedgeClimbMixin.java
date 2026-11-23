package com.evandev.spicedcider.mixin.getoverit;

import com.evandev.spicedcider.compat.getoverit.LedgeClimb;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("getoverit")
@Mixin(Player.class)
public abstract class PlayerLedgeClimbMixin {

    @WrapMethod(method = "travel")
    private void spicedcider$ledgeClimb(Vec3 travelVector, Operation<Void> original) {
        Player player = (Player) (Object) this;
        if (!player.isLocalPlayer() || !LedgeClimb.enabled()) {
            original.call(travelVector);
            return;
        }
        LedgeClimb.travel(player, travelVector, () -> original.call(travelVector));
    }
}

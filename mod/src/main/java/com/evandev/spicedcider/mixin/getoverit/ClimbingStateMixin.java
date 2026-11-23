package com.evandev.spicedcider.mixin.getoverit;

import com.evandev.spicedcider.compat.getoverit.LedgeClimb;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import kobber.getoverit.api.ClimbingState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("getoverit")
@Mixin(value = ClimbingState.class, remap = false)
public class ClimbingStateMixin {

    @WrapMethod(method = "isClimbing")
    private static boolean spicedcider$ledgeClimbState(Player player, Operation<Boolean> original) {
        return LedgeClimb.enabled() ? LedgeClimb.isClimbing(player) : original.call(player);
    }

    @WrapMethod(method = "setClimbing")
    private static void spicedcider$ignoreOwnClimbing(Player player, boolean climbing, Operation<Void> original) {
        if (!LedgeClimb.enabled()) original.call(player, climbing);
    }
}

package com.evandev.spicedcider.mixin.getoverit;

import com.evandev.spicedcider.compat.getoverit.LedgeClimb;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import kobber.getoverit.PlayerWithClimbingAnim;
import kobber.getoverit.api.ClimbingState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("getoverit")
@Mixin(value = ClimbingState.class, remap = false)
public class ClimbingStateMixin {

    @WrapMethod(method = "setClimbing")
    private static void spicedcider$gateClimbStart(Player player, boolean climbing, Operation<Void> original) {
        if (climbing && player.isLocalPlayer() && !ClimbingState.isClimbing(player)) {
            if (!LedgeClimb.allowStart(player)) {
                if (player instanceof PlayerWithClimbingAnim anim) anim.getoverit$setClimbAnimTime(0.0);
                return;
            }
            original.call(player, true);
            LedgeClimb.started();
            return;
        }
        original.call(player, climbing);
    }
}

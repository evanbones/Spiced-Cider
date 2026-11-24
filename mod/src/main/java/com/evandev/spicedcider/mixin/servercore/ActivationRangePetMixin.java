package com.evandev.spicedcider.mixin.servercore;

import com.evandev.spicedcider.content.handler.FollowingPets;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("servercore")
@Mixin(targets = "me.wesley1808.servercore.common.activation_range.ActivationRange", remap = false)
public class ActivationRangePetMixin {

    @ModifyReturnValue(method = "shouldTick", at = @At("RETURN"))
    private static boolean spicedcider$tickFollowingPets(boolean original, @Local(argsOnly = true) Entity entity) {
        return original || FollowingPets.isFollowingOwner(entity);
    }
}

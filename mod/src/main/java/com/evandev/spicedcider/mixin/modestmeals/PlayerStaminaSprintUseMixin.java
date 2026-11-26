package com.evandev.spicedcider.mixin.modestmeals;

import com.evandev.spicedcider.compat.vanillabackport.VanillaBackportCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("modest_meals")
@Mixin(targets = "com.evandev.modest_meals.stamina.PlayerStamina", remap = false)
public class PlayerStaminaSprintUseMixin {

    // TODO: should probably make this native?
    @WrapOperation(method = "isAtFullSprint", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isUsingItem()Z", remap = true))
    private boolean spicedcider$sprintWhileUsing(Player player, Operation<Boolean> original) {
        return original.call(player) && !VanillaBackportCompat.canSprintWhileUsing(player.getUseItem());
    }
}

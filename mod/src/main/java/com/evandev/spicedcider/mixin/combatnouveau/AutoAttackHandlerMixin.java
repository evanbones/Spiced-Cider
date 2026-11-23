package com.evandev.spicedcider.mixin.combatnouveau;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import fuzs.combatnouveau.client.handler.AutoAttackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("combatnouveau")
@Mixin(value = AutoAttackHandler.class, remap = false)
public abstract class AutoAttackHandlerMixin {

    @ModifyReturnValue(method = "readyForAutoAttack", at = @At("RETURN"))
    private static boolean spicedcider$onlyAutoAttackEntities(boolean original) {
        HitResult hit = Minecraft.getInstance().hitResult;
        return original && hit != null && hit.getType() == HitResult.Type.ENTITY;
    }
}

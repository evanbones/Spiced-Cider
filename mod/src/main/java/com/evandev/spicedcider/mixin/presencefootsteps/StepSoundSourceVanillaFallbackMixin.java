package com.evandev.spicedcider.mixin.presencefootsteps;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("presencefootsteps")
@Mixin(targets = "eu.ha3.presencefootsteps.sound.StepSoundSource$Container", remap = false)
public class StepSoundSourceVanillaFallbackMixin {
    @Shadow
    @Final
    private LivingEntity entity;

    @ModifyReturnValue(method = "isStepBlocked", at = @At("RETURN"))
    private boolean spicedcider$vanillaStepsForNone(boolean blocked) {
        return blocked && PresenceFootsteps.getInstance().engine.getIsolator().locomotions().lookup(this.entity) != Locomotion.NONE;
    }
}

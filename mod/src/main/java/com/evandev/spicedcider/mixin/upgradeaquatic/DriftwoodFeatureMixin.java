package com.evandev.spicedcider.mixin.upgradeaquatic;

import com.evandev.spicedcider.content.worldgen.StrippedDriftwoodFeature;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("upgrade_aquatic")
@Mixin(targets = "com.teamabnormals.upgrade_aquatic.common.levelgen.feature.DriftwoodFeature", remap = false)
public class DriftwoodFeatureMixin {

    @WrapOperation(method = {"place", "placeDriftwoodLog", "placeBranch"}, at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/registries/DeferredBlock;get()Ljava/lang/Object;"))
    private Object spicedcider$strippedLog(DeferredBlock<?> block, Operation<Object> original) {
        return StrippedDriftwoodFeature.swap(original.call(block));
    }
}

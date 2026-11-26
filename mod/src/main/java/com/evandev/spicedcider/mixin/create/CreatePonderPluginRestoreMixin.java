package com.evandev.spicedcider.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import net.createmod.ponder.api.level.PonderLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("create")
@Mixin(value = CreatePonderPlugin.class, remap = false)
public class CreatePonderPluginRestoreMixin {

    @WrapOperation(method = "onPonderLevelRestore", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/ponder/PonderWorldBlockEntityFix;fixControllerBlockEntities(Lnet/createmod/ponder/api/level/PonderLevel;)V"))
    private void spicedcider$fixOnce(PonderLevel level, Operation<Void> original) {
        if ((Object) getClass() == CreatePonderPlugin.class) {
            original.call(level);
        }
    }
}

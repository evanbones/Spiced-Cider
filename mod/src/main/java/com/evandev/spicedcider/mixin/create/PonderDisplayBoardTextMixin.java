package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.ponder.PonderSubstitutes;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("create")
@Mixin(targets = "com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions", remap = false)
public class PonderDisplayBoardTextMixin {

    @ModifyVariable(method = "setDisplayBoardText", at = @At("HEAD"), argsOnly = true)
    private Component spicedcider$substituteRemoved(Component text) {
        return PonderSubstitutes.text(text);
    }
}

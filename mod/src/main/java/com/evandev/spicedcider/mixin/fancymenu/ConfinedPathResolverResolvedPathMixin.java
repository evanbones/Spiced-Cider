package com.evandev.spicedcider.mixin.fancymenu;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@IfModLoaded("fancymenu")
@Mixin(targets = "de.keksuccino.fancymenu.util.file.ConfinedPathResolver$ResolvedPath", remap = false)
public abstract class ConfinedPathResolverResolvedPathMixin {

    @Shadow
    @Final
    private Path path;

    @Inject(method = "revalidate", at = @At("HEAD"), cancellable = true)
    private void spicedcider$bypassSymlinkEscapeCheck(CallbackInfoReturnable<Path> cir) {
        cir.setReturnValue(this.path);
    }
}

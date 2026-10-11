package com.evandev.spicedcider.mixin.voidwater;

import com.evandev.spicedcider.voidwater.client.ClientVoidTrails;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("sodium")
@Mixin(DefaultFluidRenderer.class)
public class SodiumFluidRendererMixin {

    @ModifyVariable(method = "render", at = @At("STORE"), name = "cullDown")
    private boolean spicedcider$hideBottomAboveVoidTrail(boolean cullDown, @Local(argsOnly = true, ordinal = 0) BlockPos pos) {
        return cullDown || ClientVoidTrails.hidesBottomFace(pos);
    }
}

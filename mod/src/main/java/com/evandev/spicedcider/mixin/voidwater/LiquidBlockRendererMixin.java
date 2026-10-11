package com.evandev.spicedcider.mixin.voidwater;

import com.evandev.spicedcider.voidwater.client.ClientVoidTrails;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {

    @ModifyVariable(method = "tesselate", at = @At("STORE"), ordinal = 2)
    private boolean spicedcider$hideBottomAboveVoidTrail(boolean renderDown, @Local(argsOnly = true) BlockPos pos) {
        return renderDown && !ClientVoidTrails.hidesBottomFace(pos);
    }
}

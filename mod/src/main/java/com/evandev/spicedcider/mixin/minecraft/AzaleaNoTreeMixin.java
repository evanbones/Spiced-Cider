package com.evandev.spicedcider.mixin.minecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.AzaleaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AzaleaBlock.class)
public class AzaleaNoTreeMixin {

    @ModifyReturnValue(method = "isValidBonemealTarget", at = @At("RETURN"))
    private boolean spicedcider$noAzaleaTrees(boolean original) {
        return false;
    }
}

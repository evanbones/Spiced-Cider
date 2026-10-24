package com.evandev.spicedcider.mixin.offgrid;

import com.evandev.spicedcider.registry.ModTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.offgrid.world.Support;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@Mixin(value = Support.class, remap = false)
public class SupportDirectionMixin {

    @ModifyReturnValue(method = "direction", at = @At("RETURN"))
    private static Direction spicedcider$hangFromCeiling(Direction original, BlockState state) {
        return state.is(ModTags.Blocks.HANGS_FROM_CEILING) ? Direction.UP : original;
    }
}

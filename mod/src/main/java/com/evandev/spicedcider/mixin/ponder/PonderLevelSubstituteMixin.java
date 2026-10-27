package com.evandev.spicedcider.mixin.ponder;

import com.evandev.spicedcider.compat.ponder.PonderSubstitutes;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("ponder")
@Mixin(value = PonderLevel.class, remap = false)
public class PonderLevelSubstituteMixin {

    @ModifyVariable(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", at = @At("HEAD"), argsOnly = true)
    private BlockState spicedcider$substituteRemoved(BlockState state) {
        return PonderSubstitutes.block(state);
    }
}

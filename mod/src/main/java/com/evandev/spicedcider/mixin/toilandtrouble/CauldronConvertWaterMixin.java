package com.evandev.spicedcider.mixin.toilandtrouble;

import cc.cassian.cauldrons.blocks.BrewingCauldronBlock;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModLoaded("toil_and_trouble")
@Mixin(targets = "cc.cassian.cauldrons.core.CauldronModHelpers", remap = false)
public class CauldronConvertWaterMixin {

    @ModifyVariable(method = "setBlockAndUpdate", at = @At("HEAD"), argsOnly = true)
    private static BlockState spicedcider$keepWater(BlockState state, Level level, BlockPos pos) {
        if (state.getBlock() instanceof BrewingCauldronBlock
                && state.getValue(BrewingCauldronBlock.CONTENTS) == BrewingCauldronBlock.ContentsProperty.EMPTY
                && state.getValue(BrewingCauldronBlock.POTION_QUANTITY) > 0
                && level.getBlockState(pos).is(Blocks.WATER_CAULDRON)) {
            return state.setValue(BrewingCauldronBlock.CONTENTS, BrewingCauldronBlock.ContentsProperty.WATER);
        }
        return state;
    }
}

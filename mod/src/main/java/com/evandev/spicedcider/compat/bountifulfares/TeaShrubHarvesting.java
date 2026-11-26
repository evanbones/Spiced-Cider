package com.evandev.spicedcider.compat.bountifulfares;

import net.hecco.bountifulfares.definition.block.custom.TeaShrubBlock;
import net.hecco.bountifulfares.registry.content.BFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class TeaShrubHarvesting {
    private static final int AGE_AFTER_SHEARING = 2;

    public static boolean isTeaShrub(BlockState state) {
        return state.getBlock() instanceof TeaShrubBlock;
    }

    public static boolean isGrown(BlockState state) {
        return state.getValue(TeaShrubBlock.BERRIES) || state.getValue(TeaShrubBlock.AGE) == 4;
    }

    public static ItemStack produce(BlockState state, RandomSource random) {
        if (state.getValue(TeaShrubBlock.BERRIES)) {
            return new ItemStack(BFItems.TEA_BERRIES.get());
        }
        return new ItemStack(BFItems.TEA_LEAVES.get(), 3 + random.nextInt(2));
    }

    public static void pick(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!isTeaShrub(state)) return;
        if (state.getValue(TeaShrubBlock.BERRIES)) {
            level.setBlock(pos, state.setValue(TeaShrubBlock.BERRIES, false), 2);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            level.setBlock(pos, state.setValue(TeaShrubBlock.AGE, AGE_AFTER_SHEARING), 2);
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}

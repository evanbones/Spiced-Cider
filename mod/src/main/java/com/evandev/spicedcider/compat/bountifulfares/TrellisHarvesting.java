package com.evandev.spicedcider.compat.bountifulfares;

import net.hecco.bountifulfares.definition.block.custom.TrellisBlock;
import net.hecco.bountifulfares.definition.block.entity.TrellisBlockEntity;
import net.hecco.bountifulfares.definition.data.trellis.TrellisCropDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class TrellisHarvesting {
    private static final int STAGES_KEPT_AFTER_PICKING = 2;

    public static boolean isTrellis(BlockState state) {
        return state.getBlock() instanceof TrellisBlock;
    }

    @Nullable
    private static TrellisCropDefinition ripeCrop(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof TrellisBlockEntity trellis)) return null;
        TrellisCropDefinition crop = TrellisBlock.CROPS.get(trellis.getPlant());
        return crop != null && trellis.getStage() >= crop.stages() ? crop : null;
    }

    public static boolean isGrown(Level level, BlockPos pos) {
        return ripeCrop(level, pos) != null;
    }

    public static ItemStack produce(Level level, BlockPos pos, RandomSource random) {
        TrellisCropDefinition crop = ripeCrop(level, pos);
        if (crop == null) return ItemStack.EMPTY;
        int min = Math.min(crop.minDrops(), crop.maxDrops());
        int max = Math.max(crop.minDrops(), crop.maxDrops());
        return new ItemStack(crop.produce(), min + random.nextInt(max - min + 1));
    }

    public static void pick(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof TrellisBlockEntity trellis)) return;
        trellis.setStage(Math.max(trellis.getStage() - STAGES_KEPT_AFTER_PICKING, 0));
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 1.0F + level.random.nextFloat() / 5.0F);
    }
}

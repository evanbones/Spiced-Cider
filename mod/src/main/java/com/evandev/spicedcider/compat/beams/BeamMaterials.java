package com.evandev.spicedcider.compat.beams;

import com.evandev.spicedcider.registry.ModBlocks;
import com.syl.beams.blockentities.BeamBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BeamMaterials {

    public static boolean isCastIron(BeamBlockEntity.BeamLink link) {
        return link != null && BeamsCompat.CAST_IRON.equals(link.woodType);
    }

    private static BlockState castIronBlock() {
        return ModBlocks.CAST_IRON_BLOCK.get().defaultBlockState();
    }

    public static float castIronDestroyProgress(Player player, BlockGetter level, BlockPos pos) {
        return castIronBlock().getDestroyProgress(player, level, pos);
    }

    public static boolean canHarvestCastIron(Player player) {
        return player.hasCorrectToolForDrops(castIronBlock());
    }
}

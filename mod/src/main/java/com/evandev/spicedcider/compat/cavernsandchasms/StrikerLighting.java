package com.evandev.spicedcider.compat.cavernsandchasms;

import com.teamabnormals.caverns_and_chasms.common.block.CoalBlock;
import com.teamabnormals.caverns_and_chasms.common.block.Sparkler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class StrikerLighting {

    public static boolean light(Level level, BlockPos pos, @Nullable Player player) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof Sparkler sparkler) {
            if (!level.isClientSide()) {
                if (state.getValue(BlockStateProperties.LIT)) {
                    sparkler.explodeSparkler(state, level, pos);
                } else {
                    level.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 11);
                }
            }
        } else if (state.getBlock() instanceof CoalBlock && !state.getValue(CoalBlock.LIT) && !state.getValue(CoalBlock.WATERLOGGED)) {
            if (!level.isClientSide()) level.setBlock(pos, state.setValue(CoalBlock.LIT, true), 11);
        } else {
            return false;
        }
        level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return true;
    }
}

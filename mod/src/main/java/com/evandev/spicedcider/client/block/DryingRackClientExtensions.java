package com.evandev.spicedcider.client.block;

import com.evandev.spicedcider.content.block.TanningRackBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

public class DryingRackClientExtensions implements IClientBlockExtensions {
    private static final double THIN_MIN = 6.0 / 16.0;
    private static final double THIN_SIZE = 4.0 / 16.0;

    @Override
    public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
        if (!(level instanceof ClientLevel client) || !state.hasProperty(TanningRackBlock.HALF)) return false;
        BlockPos lower = state.getValue(TanningRackBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        boolean northSouth = state.getValue(TanningRackBlock.FACING).getAxis() == Direction.Axis.Z;
        for (int half = 0; half < 2; half++) {
            BlockPos at = lower.above(half);
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    for (int t = 0; t < 2; t++) {
                        double along = (x + 0.5) / 4.0;
                        double up = (y + 0.5) / 4.0;
                        double across = THIN_MIN + (t + 0.5) / 2.0 * THIN_SIZE;
                        double px = northSouth ? along : across;
                        double pz = northSouth ? across : along;
                        manager.add(new TerrainParticle(client, at.getX() + px, at.getY() + up, at.getZ() + pz,
                                px - 0.5, up - 0.5, pz - 0.5, state, at).updateSprite(state, at));
                    }
                }
            }
        }
        return true;
    }
}

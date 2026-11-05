package com.evandev.spicedcider.content.block.fire;

import com.evandev.spicedcider.registry.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SulfurWallTorchBlock extends WallTorchBlock {
    public SulfurWallTorchBlock(Properties properties) {
        super(ParticleTypes.FLAME, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, @NotNull RandomSource random) {
        Direction opposite = state.getValue(FACING).getOpposite();
        double x = pos.getX() + 0.5 + 0.27 * opposite.getStepX();
        double y = pos.getY() + 0.7 + 0.22;
        double z = pos.getZ() + 0.5 + 0.27 * opposite.getStepZ();
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
        level.addParticle(ModParticleTypes.SULFUR_FIRE_FLAME.get(), x, y, z, 0.0, 0.0, 0.0);
    }
}

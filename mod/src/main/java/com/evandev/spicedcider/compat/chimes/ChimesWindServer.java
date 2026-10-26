package com.evandev.spicedcider.compat.chimes;

import com.cicada.chimes.ChimesSounds;
import com.cicada.chimes.ChimesStates;
import com.cicada.chimes.block.WindBellBlock;
import com.cicada.chimes.block.WindChimeBlock;
import com.cicada.chimes.block.entity.WindBellBE;
import com.cicada.chimes.block.entity.WindChimeBE;
import com.macatmovie.livingwind.api.WindApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public final class ChimesWindServer {
    private static final float BASELINE_STRENGTH = 0.3F;
    private static final float BASELINE_CHANCE = 0.0006F;
    private static final float MAX_CHANCE = 0.02F;

    public static void tickChime(WindChimeBE chime, RandomSource random) {
        if (!(chime.getLevel() instanceof ServerLevel level)) return;
        BlockState state = chime.getBlockState();
        if (!(state.getBlock() instanceof WindChimeBlock block) || state.getValue(WindChimeBlock.HALF) != DoubleBlockHalf.UPPER) return;
        if (state.hasProperty(ChimesStates.SILENCED) && state.getValue(ChimesStates.SILENCED)) return;
        if (chime.isSwinging && chime.interactTicks < 60) return;
        BlockPos pos = chime.getBlockPos();
        if (!gust(level, pos, random) || !exposed(level, pos)) return;
        block.setChiming(state, level, pos);
    }

    public static void tickBell(WindBellBE bell, RandomSource random) {
        if (!(bell.getLevel() instanceof ServerLevel level)) return;
        BlockState state = bell.getBlockState();
        if (!(state.getBlock() instanceof WindBellBlock block)) return;
        if (bell.isSwinging && bell.chimeTicks < 60) return;
        BlockPos pos = bell.getBlockPos();
        if (!gust(level, pos, random) || !exposed(level, pos)) return;
        block.chime(level, pos);
        level.playSound(null, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, ChimesSounds.GLASSCHIME, SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    private static boolean gust(ServerLevel level, BlockPos pos, RandomSource random) {
        float strength = WindApi.getAt(level, pos).strength() / BASELINE_STRENGTH;
        float chance = Mth.clamp(BASELINE_CHANCE * strength * strength * strength, 0.0F, MAX_CHANCE);
        return random.nextFloat() < chance;
    }

    private static boolean exposed(ServerLevel level, BlockPos pos) {
        if (level.canSeeSky(pos)) return true;
        int open = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(direction)).isAir() && level.getBlockState(pos.relative(direction, 2)).isAir()) open++;
        }
        return open >= 2;
    }
}

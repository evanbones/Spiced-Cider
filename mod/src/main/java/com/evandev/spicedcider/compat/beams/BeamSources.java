package com.evandev.spicedcider.compat.beams;

import com.syl.beams.blockentities.BeamBlockEntity;
import com.syl.beams.blocks.beam.BeamAnchorBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class BeamSources {
    public static long generation() {
        return BeamBlockEntity.topologyGeneration();
    }

    public static List<Vec3[]> beams(Level level) {
        List<Vec3[]> beams = new ArrayList<>();
        Iterable<BeamBlockEntity> instances = level.isClientSide ? BeamBlockEntity.clientInstances() : BeamBlockEntity.serverInstances(level);
        for (BeamBlockEntity be : instances) {
            if (be.isRemoved() || be.getLevel() != level) continue;
            for (BeamBlockEntity.BeamLink link : be.getLinks()) {
                if (link.isStart && link.otherVec != null) beams.add(new Vec3[]{link.selfVec, link.otherVec});
            }
        }
        return beams;
    }

    public static boolean isAnchor(BlockState state) {
        return state.getBlock() instanceof BeamAnchorBlock;
    }
}

package com.evandev.spicedcider.content.block.fire;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SulfurCampfireBlock extends CampfireBlock {
    public SulfurCampfireBlock(Properties properties) {
        super(false, 1, properties);
    }

    @Override
    protected void entityInside(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (state.getValue(LIT)) SulfurFumes.expose(entity);
        super.entityInside(state, level, pos, entity);
    }
}

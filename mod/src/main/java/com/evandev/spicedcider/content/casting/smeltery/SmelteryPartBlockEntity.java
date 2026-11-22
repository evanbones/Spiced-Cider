package com.evandev.spicedcider.content.casting.smeltery;

import com.evandev.spicedcider.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SmelteryPartBlockEntity extends BlockEntity {
    public SmelteryPartBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMELTERY_PART.get(), pos, state);
    }
}

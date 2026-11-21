package com.evandev.spicedcider.compat.bountifulfares;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class JackOStrawRotation {

    public static final IntegerProperty OFFSET = IntegerProperty.create("rotation_offset", 0, 3);

    public static BlockState place(BlockState state, BlockPlaceContext context) {
        if (state == null || !SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.jackOStrawRotation, false)) return state;
        int segment = RotationSegment.convertToSegment(context.getRotation() + 180.0F);
        Direction facing = Direction.fromYRot((segment & ~3) * 22.5F);
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing).setValue(OFFSET, segment & 3);
    }
}

package com.evandev.spicedcider.mixin.blockgrid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SignBlock.class)
public class StandingSignModelShapeMixin {
    @Unique
    private static final int spicedcider$ROTATIONS = 16;
    @Unique
    private static final double spicedcider$PIXEL_CENTER = 8.0D;
    @Unique
    private static final double spicedcider$POST_HALF = 1.0D;
    @Unique
    private static final double spicedcider$POST_TOP = 7.0D;
    @Unique
    private static final double spicedcider$BOARD_HALF_WIDTH = 8.0D;
    @Unique
    private static final double spicedcider$BOARD_HALF_DEPTH = 1.0D;
    @Unique
    private static final double spicedcider$BOARD_TOP = 16.0D;

    @Unique
    private static final VoxelShape[] spicedcider$SHAPES = spicedcider$buildShapes();

    @Unique
    private static VoxelShape[] spicedcider$buildShapes() {
        VoxelShape[] shapes = new VoxelShape[spicedcider$ROTATIONS];
        for (int rotation = 0; rotation < spicedcider$ROTATIONS; rotation++) {
            double angle = Math.toRadians(rotation * (360.0D / spicedcider$ROTATIONS));
            double cos = Math.abs(Math.cos(angle));
            double sin = Math.abs(Math.sin(angle));
            shapes[rotation] = Shapes.or(
                    spicedcider$spun(spicedcider$POST_HALF, spicedcider$POST_HALF, cos, sin, 0.0D, spicedcider$POST_TOP),
                    spicedcider$spun(spicedcider$BOARD_HALF_WIDTH, spicedcider$BOARD_HALF_DEPTH, cos, sin, spicedcider$POST_TOP, spicedcider$BOARD_TOP));
        }
        return shapes;
    }

    @Unique
    private static VoxelShape spicedcider$spun(double halfWidth, double halfDepth, double cos, double sin, double minY, double maxY) {
        double spanX = halfWidth * cos + halfDepth * sin;
        double spanZ = halfWidth * sin + halfDepth * cos;
        return Block.box(
                spicedcider$PIXEL_CENTER - spanX, minY, spicedcider$PIXEL_CENTER - spanZ,
                spicedcider$PIXEL_CENTER + spanX, maxY, spicedcider$PIXEL_CENTER + spanZ);
    }

    @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
    private VoxelShape spicedcider$modelAccurateStandingShape(VoxelShape original, BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!(state.getBlock() instanceof StandingSignBlock) || !state.hasProperty(BlockStateProperties.ROTATION_16)) {
            return original;
        }
        return spicedcider$SHAPES[state.getValue(BlockStateProperties.ROTATION_16)];
    }
}

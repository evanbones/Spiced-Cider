package com.evandev.spicedcider.content.waterlogging;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

public final class ExtraWaterlogging {

    private static final Set<String> CLASSES = load();

    private static Set<String> load() {
        try (InputStream in = ExtraWaterlogging.class.getResourceAsStream("/spicedcider/extra_waterloggable_blocks.txt")) {
            if (in == null) return Set.of();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            return reader.lines().map(String::trim).filter(line -> !line.isEmpty() && !line.startsWith("#")).collect(Collectors.toUnmodifiableSet());
        } catch (IOException e) {
            throw new IllegalStateException("Could not read extra waterloggable blocks", e);
        }
    }

    public static boolean listed(Class<?> type) {
        return CLASSES.contains(type.getName());
    }

    public static boolean isExtra(Block block) {
        return ((ExtraWaterloggable) block).spicedcider$isExtraWaterloggable();
    }

    public static boolean isWaterlogged(BlockState state) {
        return isExtra(state.getBlock()) && state.getValue(BlockStateProperties.WATERLOGGED);
    }

    public static boolean isCandidate(Block block) {
        if (block instanceof LiquidBlockContainer || block instanceof BucketPickup || block instanceof MovingPistonBlock) return false;
        if (block.defaultBlockState().hasProperty(BlockStateProperties.WATERLOGGED) && !isExtra(block)) return false;
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            if (isExtra(block) && state.getValue(BlockStateProperties.WATERLOGGED)) continue;
            if (keepsWaterOut(state)) return true;
        }
        return false;
    }

    private static boolean keepsWaterOut(BlockState state) {
        if (!state.blocksMotion() || state.is(Blocks.NETHER_PORTAL) || state.is(Blocks.END_PORTAL) || state.is(Blocks.END_GATEWAY) || state.is(Blocks.STRUCTURE_VOID)) return false;
        try {
            return !Block.isShapeFullBlock(state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
        } catch (RuntimeException e) {
            return false;
        }
    }
}

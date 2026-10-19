package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class EyeblossomLightEvents {
    private static final int RADIUS = 8;
    private static Set<Block> eyeblossoms;

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getPlacedBlock().getLightEmission(level, event.getPos()) > 0) {
            recheckNear(level, event.getPos());
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level && event.getState().getLightEmission(level, event.getPos()) > 0) {
            recheckNear(level, event.getPos());
        }
    }

    private static void recheckNear(ServerLevel level, BlockPos origin) {
        Set<Block> blocks = eyeblossoms();
        if (blocks.isEmpty()) return;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-RADIUS, -RADIUS, -RADIUS), origin.offset(RADIUS, RADIUS, RADIUS))) {
            BlockState state = level.getBlockState(pos);
            if (blocks.contains(state.getBlock()) && !level.getBlockTicks().hasScheduledTick(pos, state.getBlock())) {
                level.scheduleTick(pos.immutable(), state.getBlock(), 10 + level.random.nextInt(20));
            }
        }
    }

    private static Set<Block> eyeblossoms() {
        if (eyeblossoms == null) {
            eyeblossoms = Stream.of("open_eyeblossom", "closed_eyeblossom", "potted_open_eyeblossom", "potted_closed_eyeblossom")
                    .map(name -> BuiltInRegistries.BLOCK.get(ResourceLocation.withDefaultNamespace(name)))
                    .filter(block -> block != Blocks.AIR)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return eyeblossoms;
    }
}

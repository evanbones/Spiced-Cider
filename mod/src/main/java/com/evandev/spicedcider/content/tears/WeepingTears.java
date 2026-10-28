package com.evandev.spicedcider.content.tears;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CryingObsidianBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.CauldronFluidContent;

public class WeepingTears {
    public static final TagKey<Block> WEEPS_TEARS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "weeps_dimensional_tears"));
    private static final ResourceLocation TEARS = ResourceLocation.fromNamespaceAndPath("dimensional_tears", "dimensional_tears");
    private static final float DRIP_CHANCE = 0.17578125F;
    private static final int MAX_DISTANCE = 11;

    public static boolean canWeep(Block block) {
        return block instanceof CryingObsidianBlock || block.getClass().getName().startsWith("com.davigj.frame_changer.common.block.Crying");
    }

    public static void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.is(WEEPS_TEARS) || random.nextFloat() >= DRIP_CHANCE) return;
        Fluid tears = BuiltInRegistries.FLUID.get(TEARS);
        CauldronFluidContent content = tears == Fluids.EMPTY ? null : CauldronFluidContent.getForFluid(tears);
        if (content == null) return;

        BlockPos.MutableBlockPos cursor = pos.mutable();
        for (int i = 0; i < MAX_DISTANCE; i++) {
            cursor.move(0, -1, 0);
            BlockState below = level.getBlockState(cursor);
            if (below.isAir()) continue;
            BlockState filled;
            if (below.is(Blocks.CAULDRON)) {
                filled = content.block.defaultBlockState();
                if (content.levelProperty != null) filled = filled.setValue(content.levelProperty, 1);
            } else if (below.is(content.block) && content.levelProperty != null && content.currentLevel(below) < content.maxLevel) {
                filled = below.setValue(content.levelProperty, content.currentLevel(below) + 1);
            } else {
                return;
            }
            BlockPos target = cursor.immutable();
            level.setBlockAndUpdate(target, filled);
            level.levelEvent(LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON, target, 0);
            level.gameEvent(GameEvent.BLOCK_CHANGE, target, GameEvent.Context.of(filled));
            return;
        }
    }
}

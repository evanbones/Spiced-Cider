package com.evandev.spicedcider.content.tears;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.dripping.DripCatching;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CryingObsidianBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class WeepingTears {
    public static final TagKey<Block> WEEPS_TEARS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "weeps_dimensional_tears"));
    private static final ResourceLocation TEARS = ResourceLocation.fromNamespaceAndPath("dimensional_tears", "dimensional_tears");
    private static final float DRIP_CHANCE = 0.17578125F;
    private static final int AMOUNT = 250;

    public static boolean canWeep(Block block) {
        return block instanceof CryingObsidianBlock || block.getClass().getName().startsWith("com.davigj.frame_changer.common.block.Crying");
    }

    public static void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.is(WEEPS_TEARS) || random.nextFloat() >= DRIP_CHANCE) return;
        Fluid tears = BuiltInRegistries.FLUID.get(TEARS);
        if (tears == Fluids.EMPTY) return;
        BlockPos target = DripCatching.findCatcher(level, pos);
        if (target != null && DripCatching.fill(level, target, new FluidStack(tears, AMOUNT))) {
            level.levelEvent(LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON, target, 0);
        }
    }
}

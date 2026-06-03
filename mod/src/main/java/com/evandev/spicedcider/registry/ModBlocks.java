package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.block.TemporaryCobwebBlock;
import com.evandev.spicedcider.content.block.WorkstoneBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpicedCider.MOD_ID);

    public static final DeferredBlock<Block> WORKSTONE = BLOCKS.register("workstone",
            () -> new WorkstoneBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> TEMPORARY_COBWEB = BLOCKS.register("temporary_cobweb",
            () -> new TemporaryCobwebBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBWEB)));

}
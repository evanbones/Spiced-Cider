package com.evandev.spicedcider.compat.firevariants;

import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModFireBlocks;
import com.teamabnormals.environmental.common.block.DwarfSpruceHeadBlock;
import com.teamabnormals.environmental.common.block.DwarfSprucePlantBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;

public class EnvironmentalFireBlocks {
    public static DeferredBlock<Block> dwarfSpruceSulfurTorch;
    public static DeferredBlock<Block> dwarfSprucePlantSulfurTorch;

    public static void register() {
        dwarfSpruceSulfurTorch = ModBlocks.BLOCKS.register("dwarf_spruce_sulfur_torch",
                () -> new DwarfSpruceHeadBlock(properties(), () -> ModFireBlocks.SULFUR_TORCH_ITEM.get()));
        dwarfSprucePlantSulfurTorch = ModBlocks.BLOCKS.register("dwarf_spruce_plant_sulfur_torch",
                () -> new DwarfSprucePlantBlock(properties(), () -> ModFireBlocks.SULFUR_TORCH_ITEM.get(), (DwarfSpruceHeadBlock) dwarfSpruceSulfurTorch.get()));
    }

    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.GRASS)
                .pushReaction(PushReaction.DESTROY).lightLevel(state -> ModFireBlocks.SULFUR_LIGHT);
    }
}

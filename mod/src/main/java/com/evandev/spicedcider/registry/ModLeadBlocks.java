package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.content.block.lead.LeadGrateBlock;
import com.evandev.spicedcider.content.block.lead.LeadWeightBlock;
import com.evandev.spicedcider.content.block.lead.LeadWeightItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

public class ModLeadBlocks {
    public static final float RESISTANCE = 17.0F;

    public static final SoundType GRATE_SOUND = pitched(SoundType.COPPER_GRATE, 1.1F, 0.5F);
    public static final SoundType BULB_SOUND = pitched(SoundType.COPPER_BULB, 1.1F, 0.5F);
    public static final SoundType WEIGHT_SOUND = pitched(SoundType.NETHERITE_BLOCK, 1.0F, 0.5F);

    public static final DeferredBlock<Block> LEAD_GRATE = ModBlocks.BLOCKS.register("lead_grate",
            () -> new LeadGrateBlock(lead().strength(3.0F, 10.0F).sound(GRATE_SOUND).noOcclusion()
                    .isValidSpawn((state, level, pos, type) -> false).isRedstoneConductor((state, level, pos) -> false).isSuffocating((state, level, pos) -> false).isViewBlocking((state, level, pos) -> false)));
    public static final DeferredBlock<Block> LEAD_BARS = ModBlocks.BLOCKS.register("lead_bars",
            () -> new IronBarsBlock(lead().strength(3.0F, 10.0F).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredBlock<Block> LEAD_CHAIN = ModBlocks.BLOCKS.register("lead_chain",
            () -> new ChainBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHAIN).mapColor(MapColor.COLOR_GRAY).strength(3.0F, 10.0F)));
    public static final DeferredBlock<Block> LEAD_BULB = ModBlocks.BLOCKS.register("lead_bulb",
            () -> new CopperBulbBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_BULB).mapColor(MapColor.COLOR_GRAY)
                    .strength(3.0F, RESISTANCE).sound(BULB_SOUND)));

    public static final DeferredBlock<Block> LEAD_WEIGHT = ModBlocks.BLOCKS.register("lead_weight",
            () -> new LeadWeightBlock(lead().strength(3.0F, RESISTANCE).sound(WEIGHT_SOUND).noOcclusion()));

    public static final DeferredItem<BlockItem> LEAD_GRATE_ITEM = ModItems.ITEMS.registerSimpleBlockItem(LEAD_GRATE);
    public static final DeferredItem<BlockItem> LEAD_BARS_ITEM = ModItems.ITEMS.registerSimpleBlockItem(LEAD_BARS);
    public static final DeferredItem<BlockItem> LEAD_CHAIN_ITEM = ModItems.ITEMS.registerSimpleBlockItem(LEAD_CHAIN);
    public static final DeferredItem<BlockItem> LEAD_BULB_ITEM = ModItems.ITEMS.registerSimpleBlockItem(LEAD_BULB);
    public static final DeferredItem<Item> LEAD_WEIGHT_ITEM = ModItems.ITEMS.register("lead_weight", () -> new LeadWeightItem(LEAD_WEIGHT.get(), new Item.Properties()));

    public static void register() {
    }

    public static List<DeferredBlock<Block>> blocks() {
        return List.of(LEAD_GRATE, LEAD_BARS, LEAD_CHAIN, LEAD_BULB, LEAD_WEIGHT);
    }

    public static List<DeferredItem<? extends Item>> items() {
        return List.of(LEAD_GRATE_ITEM, LEAD_BARS_ITEM, LEAD_CHAIN_ITEM, LEAD_BULB_ITEM, LEAD_WEIGHT_ITEM);
    }

    private static BlockBehaviour.Properties lead() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .instrument(NoteBlockInstrument.BELL)
                .requiresCorrectToolForDrops();
    }

    private static SoundType pitched(SoundType base, float volume, float pitch) {
        return new SoundType(base.getVolume() * volume, base.getPitch() * pitch, base.getBreakSound(), base.getStepSound(),
                base.getPlaceSound(), base.getHitSound(), base.getFallSound());
    }
}

package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.compat.firevariants.FireVariantCompat;
import com.evandev.spicedcider.content.block.fire.SulfurCampfireBlock;
import com.evandev.spicedcider.content.block.fire.SulfurFireBlock;
import com.evandev.spicedcider.content.block.fire.SulfurTorchBlock;
import com.evandev.spicedcider.content.block.fire.SulfurWallTorchBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModFireBlocks {
    public static final int SULFUR_LIGHT = 10;

    public static final DeferredBlock<Block> SULFUR_FIRE = ModBlocks.BLOCKS.register("sulfur_fire",
            () -> new SulfurFireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_FIRE).mapColor(MapColor.COLOR_BLUE).lightLevel(state -> SULFUR_LIGHT)));
    public static final DeferredBlock<Block> SULFUR_TORCH = ModBlocks.BLOCKS.register("sulfur_torch",
            () -> new SulfurTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_TORCH).lightLevel(state -> SULFUR_LIGHT)));
    public static final DeferredBlock<Block> SULFUR_WALL_TORCH = ModBlocks.BLOCKS.register("sulfur_wall_torch",
            () -> new SulfurWallTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_WALL_TORCH).lightLevel(state -> SULFUR_LIGHT).lootFrom(SULFUR_TORCH)));
    public static final DeferredBlock<Block> SULFUR_LANTERN = ModBlocks.BLOCKS.register("sulfur_lantern",
            () -> new LanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_LANTERN).lightLevel(state -> SULFUR_LIGHT)));
    public static final DeferredBlock<Block> SULFUR_CAMPFIRE = ModBlocks.BLOCKS.register("sulfur_campfire",
            () -> new SulfurCampfireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_CAMPFIRE)
                    .lightLevel(state -> state.getValue(CampfireBlock.LIT) ? SULFUR_LIGHT : 0)));

    public static final DeferredItem<Item> SULFUR_TORCH_ITEM = ModItems.ITEMS.register("sulfur_torch",
            () -> new StandingAndWallBlockItem(SULFUR_TORCH.get(), SULFUR_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN));
    public static final DeferredItem<BlockItem> SULFUR_LANTERN_ITEM = ModItems.ITEMS.registerSimpleBlockItem(SULFUR_LANTERN);
    public static final DeferredItem<BlockItem> SULFUR_CAMPFIRE_ITEM = ModItems.ITEMS.registerSimpleBlockItem(SULFUR_CAMPFIRE);

    public static void register() {
        FireVariantCompat.register();
    }
}

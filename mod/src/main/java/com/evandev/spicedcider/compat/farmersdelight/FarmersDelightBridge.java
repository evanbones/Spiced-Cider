package com.evandev.spicedcider.compat.farmersdelight;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;
import vectorwing.farmersdelight.common.item.KnifeItem;
import vectorwing.farmersdelight.common.item.MushroomColonyItem;

final class FarmersDelightBridge {

    static Item createKnife(Tier tier, Item.Properties properties) {
        return new KnifeItem(tier, properties.attributes(KnifeItem.createAttributes(tier, 0.5F, -2.0F)));
    }

    static Block createFungusColony(ResourceLocation fungus, int baseLight) {
        return new MushroomColonyBlock(DeferredHolder.create(Registries.ITEM, fungus), BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM)
                .sound(SoundType.FUNGUS)
                .lightLevel(state -> baseLight + 2 * state.getValue(MushroomColonyBlock.COLONY_AGE)));
    }

    static Item createColonyItem(Block block, Item.Properties properties) {
        return new MushroomColonyItem(block, properties);
    }
}

package com.evandev.spicedcider.content.item;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class CarvedPalePumpkinItem extends BlockItem {
    public CarvedPalePumpkinItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isEnderMask(ItemStack stack, Player player, EnderMan enderMan) {
        return true;
    }
}

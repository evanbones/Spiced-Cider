package com.evandev.spicedcider.content.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;

public class FireStrikerItem extends Item {
    public FireStrikerItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        ItemStack flint = new ItemStack(Items.FLINT_AND_STEEL);
        BlockHitResult hit = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside());

        boolean success = false;
        if (player != null) {
            BlockState state = level.getBlockState(context.getClickedPos());
            ItemInteractionResult blockResult = state.useItemOn(flint, level, player, context.getHand(), hit);
            success = blockResult.consumesAction();
        }
        if (!success) {
            UseOnContext flintContext = new UseOnContext(level, player, context.getHand(), flint, hit) {};
            success = Items.FLINT_AND_STEEL.useOn(flintContext).consumesAction();
        }

        if (success) {
            if (player != null && !player.isCreative()) {
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_FLINT_ACTIONS.contains(itemAbility);
    }
}

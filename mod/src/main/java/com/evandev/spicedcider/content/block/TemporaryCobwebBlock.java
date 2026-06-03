package com.evandev.spicedcider.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TemporaryCobwebBlock extends WebBlock {
    private static final int LIFETIME_TICKS = 100;

    public TemporaryCobwebBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, LIFETIME_TICKS);
        }
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, Player player, net.minecraft.world.level.@NotNull BlockGetter level, @NotNull BlockPos pos) {
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof SwordItem || heldItem.getItem() instanceof ShearsItem) {
            return 0.15F;
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (entity instanceof LivingEntity livingEntity && livingEntity.getType().is(EntityTypeTags.ARTHROPOD)) {
            return;
        }

        super.entityInside(state, level, pos, entity);
    }
}
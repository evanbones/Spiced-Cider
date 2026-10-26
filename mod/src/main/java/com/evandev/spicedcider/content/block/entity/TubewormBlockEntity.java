package com.evandev.spicedcider.content.block.entity;

import com.evandev.spicedcider.content.block.TubewormBlock;
import com.evandev.spicedcider.content.block.TubewormPart;
import com.evandev.spicedcider.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TubewormBlockEntity extends BlockEntity {
    public TubewormBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TUBEWORM.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T entity) {
        if (state.getValue(TubewormBlock.PART) == TubewormPart.MIDDLE || state.getValue(TubewormBlock.SHEARED)) {
            if (state.getValue(TubewormBlock.HIDING)) {
                level.setBlockAndUpdate(pos, state.setValue(TubewormBlock.HIDING, false));
            }
            return;
        }

        if (Math.floorMod(level.getGameTime() + pos.asLong(), 5L) != 0) return;

        boolean hasPlayerNearby = false;
        for (Player player : level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(4.0D))) {
            if (player.isSwimming()) {
                hasPlayerNearby = true;
                break;
            }
        }

        boolean currentlyHiding = state.getValue(TubewormBlock.HIDING);

        if (hasPlayerNearby != currentlyHiding) {
            level.setBlockAndUpdate(pos, state.setValue(TubewormBlock.HIDING, hasPlayerNearby));
        }
    }
}
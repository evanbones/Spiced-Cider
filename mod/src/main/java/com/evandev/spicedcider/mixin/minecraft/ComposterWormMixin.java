package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModItems;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ComposterBlock.class)
public class ComposterWormMixin {

    @WrapMethod(method = "empty")
    private static BlockState spicedcider$popWorms(Entity entity, BlockState state, LevelAccessor accessor, BlockPos pos, Operation<BlockState> original) {
        boolean ready = state.hasProperty(ComposterBlock.LEVEL) && state.getValue(ComposterBlock.LEVEL) == ComposterBlock.READY;
        BlockState result = original.call(entity, state, accessor, pos);
        if (ready && accessor instanceof Level level && !level.isClientSide && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.composterWorms, true) && level.random.nextFloat() < 0.3F) {
            Vec3 spot = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
            ItemEntity worms = new ItemEntity(level, spot.x(), spot.y(), spot.z(), new ItemStack(ModItems.WORM.get(), 1 + level.random.nextInt(2)));
            worms.setDefaultPickUpDelay();
            level.addFreshEntity(worms);
        }
        return result;
    }
}

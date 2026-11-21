package com.evandev.spicedcider.compat.create;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BasinEvaporation {
    public static final ResourceLocation SALT = ResourceLocation.fromNamespaceAndPath("spelunkery", "salt");

    public static boolean isHeated(BlockState below) {
        HeatLevel heat = BasinBlockEntity.getHeatLevelOf(below);
        return heat.isAtLeast(HeatLevel.KINDLED) || BasinHeat.isHeatSource(below);
    }

    public static boolean evaporate(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof BasinBlockEntity basin) || !isHeated(level.getBlockState(pos.below()))) return false;
        Item salt = BuiltInRegistries.ITEM.get(SALT);
        if (salt == Items.AIR) return false;
        List<ItemStack> result = List.of(new ItemStack(salt));
        if (basin.acceptOutputs(result, List.of(), true)) {
            basin.acceptOutputs(result, List.of(), false);
            basin.notifyUpdate();
        }
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.3F, 1.6F + level.random.nextFloat() * 0.3F);
        level.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 3, 0.15, 0.05, 0.15, 0.01);
        return true;
    }
}

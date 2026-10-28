package com.evandev.spicedcider.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class CauldronBubbles {
    private static final ResourceLocation BUBBLE = ResourceLocation.fromNamespaceAndPath("amendments", "boiling_bubble");
    private static final TagKey<Block> HEATS_CAULDRONS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("toil_and_trouble", "heats_cauldrons"));

    public static void animate(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int quantity = quantity(state);
        if (quantity <= 0 || !level.getBlockState(pos.below()).is(HEATS_CAULDRONS)) {
            return;
        }
        String contents = value(state, "contents");
        if (!contents.equals("water") && !contents.equals("potion")) {
            return;
        }
        ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.get(BUBBLE);
        if (!(type instanceof SimpleParticleType bubble)) {
            return;
        }
        int color = Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0);
        double surface = pos.getY() + (6 + 3 * quantity) / 16.0;
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + 0.1875 + random.nextFloat() * 0.625;
            double z = pos.getZ() + 0.1875 + random.nextFloat() * 0.625;
            level.addParticle(bubble, x, surface, z, color, pos.getY() + 0.3125, 0);
        }
        if (random.nextInt(4) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                    SoundSource.BLOCKS, 0.4F + random.nextFloat() * 0.2F, 0.35F + random.nextFloat() * 0.2F, false);
        }
    }

    private static int quantity(BlockState state) {
        try {
            return Integer.parseInt(value(state, "potion_quantity"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String value(BlockState state, String name) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
        return property == null ? "" : name(property, state);
    }

    private static <T extends Comparable<T>> String name(Property<T> property, BlockState state) {
        return property.getName(state.getValue(property));
    }
}

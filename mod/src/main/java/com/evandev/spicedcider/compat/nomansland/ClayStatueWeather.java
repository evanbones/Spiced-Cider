package com.evandev.spicedcider.compat.nomansland;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModItems;
import com.evandev.spicedcider.weather.CloudCover;
import com.evandev.spicedcider.weather.CloudCoverData;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class ClayStatueWeather {

    public static boolean isStatue(Entity entity) {
        return entity instanceof ItemEntity item && (isMournful(item.getItem()) || isCheerful(item.getItem()));
    }

    private static boolean isMournful(ItemStack stack) {
        return ModItems.MOURNFUL_CLAY_STATUE != null && stack.is(ModItems.MOURNFUL_CLAY_STATUE.get());
    }

    private static boolean isCheerful(ItemStack stack) {
        return ModItems.CHEERFUL_CLAY_STATUE != null && stack.is(ModItems.CHEERFUL_CLAY_STATUE.get());
    }

    public static void crumble(ServerLevel level, ItemEntity entity) {
        ItemStack stack = entity.getItem();
        boolean rain = isMournful(stack);
        if (!rain && !isCheerful(stack)) return;

        ServerLevel overworld = level.getServer().overworld();
        if (rain) {
            overworld.setWeatherParameters(0, ServerLevel.RAIN_DURATION.sample(overworld.getRandom()), true, false);
            if (SpicedCiderConfig.feature("cloudCover")) CloudCoverData.get(overworld).fastFade();
        } else {
            int clearTime = ServerLevel.RAIN_DELAY.sample(overworld.getRandom());
            overworld.setWeatherParameters(clearTime, 0, false, false);
            if (SpicedCiderConfig.feature("cloudCover")) CloudCoverData.get(overworld).command(overworld, CloudCover.CLEAR, CloudCover.CLEAR.sampleDuration(overworld.getRandom()));
        }

        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack.copyWithCount(1)),
                entity.getX(), entity.getY() + 0.25, entity.getZ(), 24, 0.15, 0.15, 0.15, 0.08);
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.DECORATED_POT_SHATTER, SoundSource.NEUTRAL, 0.8F, 1.1F);

        stack.shrink(1);
        if (stack.isEmpty()) entity.discard();
        else entity.setItem(stack);
    }
}

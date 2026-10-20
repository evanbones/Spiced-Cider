package com.evandev.spicedcider.content.haunting;

import com.evandev.spicedcider.SpicedCider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Optional;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class SoulFireHauntingEvents {
    private static final String PROGRESS = "spicedcider_haunting";

    private static boolean inSoulFire(ItemEntity item) {
        return item.level().getBlockState(item.blockPosition()).is(Blocks.SOUL_FIRE);
    }

    @SubscribeEvent
    public static void keepHauntableItems(EntityInvulnerabilityCheckEvent event) {
        if (!(event.getEntity() instanceof ItemEntity item) || !event.getSource().is(DamageTypeTags.IS_FIRE)) return;
        if (inSoulFire(item) && SoulHaunting.result(item.level(), item.getItem()).isPresent()) event.setInvulnerable(true);
    }

    @SubscribeEvent
    public static void hauntItems(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity item) || !(item.level() instanceof ServerLevel level)) return;
        if (!inSoulFire(item)) {
            item.getPersistentData().remove(PROGRESS);
            return;
        }
        if (item.tickCount % 5 != 0) return;
        ItemStack stack = item.getItem();
        Optional<ItemStack> result = SoulHaunting.result(level, stack);
        if (result.isEmpty()) return;
        item.clearFire();
        int progress = item.getPersistentData().getInt(PROGRESS) + 5;
        if (level.random.nextInt(3) == 0) {
            level.sendParticles(ParticleTypes.SOUL, item.getX(), item.getY() + 0.25, item.getZ(), 1, 0.1, 0.1, 0.1, 0.01);
        }
        if (progress < SoulHaunting.FIRE_TIME) {
            item.getPersistentData().putInt(PROGRESS, progress);
            return;
        }
        ItemStack output = result.get();
        output.setCount(Math.min(output.getMaxStackSize(), output.getCount() * stack.getCount()));
        item.setItem(output);
        item.getPersistentData().remove(PROGRESS);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, item.getX(), item.getY() + 0.25, item.getZ(), 8, 0.15, 0.15, 0.15, 0.02);
        level.playSound(null, item.blockPosition(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.BLOCKS, 0.6F, 1.0F);
    }
}

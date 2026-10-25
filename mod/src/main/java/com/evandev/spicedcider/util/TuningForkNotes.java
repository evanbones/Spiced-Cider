package com.evandev.spicedcider.util;

import com.evandev.spicedcider.compat.cavernsandchasms.TuningForkGolems;
import com.evandev.spicedcider.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class TuningForkNotes {
    private static final ResourceKey<Item> FORK_KEY = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("spelunkery", "tuning_fork"));
    private static final ResourceLocation VIBRATE = ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "item.tuning_fork.vibrate");

    public static Integer get(ItemStack stack) {
        return stack.get(ModDataComponents.FORK_NOTE.get());
    }

    public static float pitch(int note) {
        return (float) Math.pow(2.0, (note - 12) / 12.0);
    }

    public static MutableComponent name(int note) {
        return Component.translatable("item.spicedcider.tuning_fork.note." + note).append(" (" + note + ")");
    }

    public static boolean isFork(ItemStack stack) {
        return stack.getItemHolder().is(FORK_KEY);
    }

    public static void play(Level level, Entity source, double x, double y, double z, int note) {
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getOptional(VIBRATE).orElse(SoundEvents.AMETHYST_BLOCK_CHIME);
        level.playSound(null, x, y, z, sound, SoundSource.NEUTRAL, 1.0F, pitch(note));
        if (!level.isClientSide && TuningForkGolems.isLoaded()) {
            TuningForkGolems.vibrate(level, source, new Vec3(x, y, z));
        }
    }

    public static void chimeForGeode(ServerLevel level, Player player, BlockPos geode, Integer note) {
        double distance = Math.sqrt(geode.distToCenterSqr(player.position()));
        float volume = (float) Mth.clamp(1.0 - distance / 128.0, 0.2, 1.0);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, volume, note == null ? 1.0F : pitch(note));
    }

    public static void particle(Level level, double x, double y, double z, int note) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.NOTE, x, y, z, 0, note / 24.0, 0.0, 0.0, 1.0);
        } else {
            level.addParticle(ParticleTypes.NOTE, x, y, z, note / 24.0, 0.0, 0.0);
        }
    }
}

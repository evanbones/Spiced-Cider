package com.evandev.spicedcider.compat.tide;

import com.li64.tide.data.fishing.mediums.FishingMedium;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluid;

final class DimensionalTearsMedium implements FishingMedium {

    static final String ID = "dimensional_tears";

    private static final TagKey<Fluid> TEARS = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("dimensional_tears", "dimensional_tears"));

    private final ResourceLocation id = ResourceLocation.fromNamespaceAndPath("spicedcider", ID);

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public boolean canFishIn(TideFishingHook hook) {
        return hook.getFluidState() != null && hook.getFluidState().is(TEARS) && TideHookEffects.canFishTears(hook);
    }

    @Override
    public boolean isAt(BlockPos pos, ServerLevel level) {
        return level.getFluidState(pos).is(TEARS);
    }

    @Override
    public float getHeight(TideFishingHook hook) {
        return hook.getFluidState().getHeight(hook.level(), hook.blockPosition());
    }

    @Override
    public double biteTimeMultiplier() {
        return 1.3;
    }

    @Override
    public void drawAmbientSplash(TideFishingHook hook, float angleRad, float distance, double x, double y, double z) {
        if (!(hook.level() instanceof ServerLevel level)) return;
        if (!level.getFluidState(BlockPos.containing(x, y - 1.0, z)).is(TEARS)) return;
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y, z, 2 + hook.getRandom().nextInt(2), 0.1, 0.0, 0.1, 0.0);
    }

    @Override
    public void drawFishTrail(TideFishingHook hook, float angleRad, float sin, float cos, double fishX, double fishY, double fishZ) {
        if (!(hook.level() instanceof ServerLevel level)) return;
        if (!level.getFluidState(BlockPos.containing(fishX, fishY - 1.0, fishZ)).is(TEARS)) return;

        RandomSource random = hook.getRandom();
        if (random.nextFloat() < 0.15f) {
            level.sendParticles(ParticleTypes.PORTAL, fishX, fishY - 0.1, fishZ, 1, sin, 0.1, cos, 0.0);
        }
        float scaledSin = sin * 0.04f;
        float scaledCos = cos * 0.04f;
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, fishX, fishY, fishZ, 0, scaledCos, 0.01, -scaledSin, 1.0);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, fishX, fishY, fishZ, 0, -scaledCos, 0.01, scaledSin, 1.0);
    }

    @Override
    public void onFishBite(TideFishingHook hook) {
        if (!(hook.level() instanceof ServerLevel level)) return;

        RandomSource random = hook.getRandom();
        hook.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25f, 0.6f + (random.nextFloat() - random.nextFloat()) * 0.2f);

        double y = hook.getY() + 0.5;
        int count = (int) (1.0f + hook.getBbWidth() * 20.0f);
        level.sendParticles(ParticleTypes.PORTAL, hook.getX(), y, hook.getZ(), count, hook.getBbWidth(), 0.0, hook.getBbWidth(), 0.2);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, hook.getX(), y, hook.getZ(), count, hook.getBbWidth(), 0.0, hook.getBbWidth(), 0.2);
    }
}

package com.evandev.spicedcider.content.worm;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModAttachments;
import com.evandev.spicedcider.registry.ModBlocks;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class WormHuntEvents {
    private static final ResourceLocation FOUND_PARTICLE = ResourceLocation.fromNamespaceAndPath("environmental", "pig_finds_truffle");
    private static final int HUNT_TIME = 4800;

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Animal seeker) || !seeker.getType().is(ModTags.EntityTypes.WORM_SEEKERS)) return;
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModTags.Items.WORM_SEEKER_ITEMS) || !seeker.isAlive() || seeker.isBaby() || seeker.isLeashed()) return;
        if (seeker.hasData(ModAttachments.WORM_HUNT) && !seeker.getData(ModAttachments.WORM_HUNT).isIdle()) return;

        if (event.getLevel() instanceof ServerLevel level) {
            if (level.dimensionType().natural()) {
                seeker.getData(ModAttachments.WORM_HUNT).time = HUNT_TIME;
                if (!event.getEntity().getAbilities().instabuild) stack.shrink(1);
                burst(level, seeker, foundParticle(), 7);
            } else {
                burst(level, seeker, ParticleTypes.SMOKE, 7);
            }
        }
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof Animal seeker && seeker.getType().is(ModTags.EntityTypes.WORM_SEEKERS)
                && seeker.goalSelector.getAvailableGoals().stream().noneMatch(goal -> goal.getGoal() instanceof WormHuntGoal)) {
            seeker.goalSelector.addGoal(2, new WormHuntGoal(seeker));
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || !(entity instanceof Animal seeker) || !seeker.hasData(ModAttachments.WORM_HUNT)) return;
        WormHunt hunt = seeker.getData(ModAttachments.WORM_HUNT);
        if (hunt.isIdle()) return;

        if (hunt.time == 0 || (hunt.target != null && !level.getBlockState(hunt.target).is(ModBlocks.WORMY_DIRT.get()))) {
            hunt.target = null;
            if (hunt.time > 0) hunt.time = Math.max(-400, -hunt.time);
        } else if (hunt.time > 0) {
            hunt.time--;
        } else {
            hunt.time++;
            if (hunt.target != null && hunt.time % 10 == 0) burst(level, seeker, foundParticle(), 1);
        }

        hunt.sniffTime++;
        if (hunt.looking && seeker.getRandom().nextInt(90) < hunt.sniffTime) {
            seeker.playAmbientSound();
            hunt.sniffTime = -90;
        }
    }

    private static ParticleOptions foundParticle() {
        return BuiltInRegistries.PARTICLE_TYPE.get(FOUND_PARTICLE) instanceof SimpleParticleType type ? type : ParticleTypes.HAPPY_VILLAGER;
    }

    private static void burst(ServerLevel level, Entity entity, ParticleOptions particle, int count) {
        for (int i = 0; i < count; i++) {
            level.sendParticles(particle, entity.getRandomX(1.0D), entity.getRandomY() + 0.5D, entity.getRandomZ(1.0D), 1,
                    level.random.nextGaussian() * 0.02D, level.random.nextGaussian() * 0.02D, level.random.nextGaussian() * 0.02D, 0.0D);
        }
    }
}

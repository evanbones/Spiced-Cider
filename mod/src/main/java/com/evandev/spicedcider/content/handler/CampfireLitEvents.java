package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModCriteria;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CampfireLitEvents {
    private record Pending(ServerPlayer player, ServerLevel level, BlockPos pos, int ticksLeft) {
    }

    private static final List<Pending> PENDING = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            watch(player, event.getLevel(), event.getPos(), 1);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (event.getRayTraceResult() instanceof BlockHitResult hit
                && projectile.getOwner() instanceof ServerPlayer player) {
            watch(player, projectile.level(), hit.getBlockPos(), 2);
        }
    }

    private static void watch(ServerPlayer player, Level level, BlockPos pos, int ticks) {
        if (level instanceof ServerLevel serverLevel && isUnlitCampfire(level.getBlockState(pos))) {
            PENDING.add(new Pending(player, serverLevel, pos.immutable(), ticks));
        }
    }

    private static boolean isUnlitCampfire(BlockState state) {
        return state.is(BlockTags.CAMPFIRES) && state.hasProperty(CampfireBlock.LIT) && !state.getValue(CampfireBlock.LIT);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (PENDING.isEmpty()) return;
        List<Pending> retry = new ArrayList<>();
        for (Pending pending : PENDING) {
            BlockState state = pending.level().getBlockState(pending.pos());
            if (state.is(BlockTags.CAMPFIRES) && state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT)) {
                if (!pending.player().isRemoved()) {
                    ModCriteria.CAMPFIRE_LIT.get().trigger(pending.player());
                }
            } else if (pending.ticksLeft() > 1) {
                retry.add(new Pending(pending.player(), pending.level(), pending.pos(), pending.ticksLeft() - 1));
            }
        }
        PENDING.clear();
        PENDING.addAll(retry);
    }
}

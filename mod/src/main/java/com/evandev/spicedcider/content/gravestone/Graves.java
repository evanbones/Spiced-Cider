package com.evandev.spicedcider.content.gravestone;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.content.block.GraveBlock;
import com.evandev.spicedcider.content.block.GravestoneBlock;
import com.evandev.spicedcider.content.block.entity.GraveBlockEntity;
import com.evandev.spicedcider.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Graves {
    private static final int RADIUS = 2;
    private static final long TICKS_PER_HOUR = 72000L;

    @Nullable
    public static BlockPos create(ServerPlayer player, List<GraveBlockEntity.Entry> items, int experience) {
        ServerLevel level = player.serverLevel();
        BlockPos pos = findPosition(level, player.blockPosition());
        if (pos == null) return null;

        BlockState state = ModBlocks.GRAVE.get().defaultBlockState()
                .setValue(GravestoneBlock.FACING, player.getDirection().getOpposite())
                .setValue(GravestoneBlock.WATERLOGGED, level.getFluidState(pos).is(FluidTags.WATER));
        level.destroyBlock(pos, true);
        level.setBlockAndUpdate(pos, state);
        if (!(level.getBlockEntity(pos) instanceof GraveBlockEntity grave)) return null;

        grave.fill(items, experience, new ResolvableProfile(player.getGameProfile()), System.currentTimeMillis(), level.getGameTime());
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        GraveHistory.get(level.getServer()).add(player.getUUID(), GlobalPos.of(level.dimension(), pos));
        return pos.immutable();
    }

    @Nullable
    private static BlockPos findPosition(ServerLevel level, BlockPos deathPos) {
        BlockPos origin = deathPos.atY(Mth.clamp(deathPos.getY(), level.getMinBuildHeight(), level.getMaxBuildHeight() - 1));
        double bestCost = Double.MAX_VALUE;
        BlockPos best = null;
        for (BlockPos candidate : BlockPos.betweenClosed(origin.offset(-RADIUS, -RADIUS, -RADIUS), origin.offset(RADIUS, RADIUS, RADIUS))) {
            double cost = cost(level, candidate, origin);
            if (cost < bestCost) {
                bestCost = cost;
                best = candidate.immutable();
            }
        }
        return best;
    }

    private static double cost(ServerLevel level, BlockPos pos, BlockPos origin) {
        if (level.isOutsideBuildHeight(pos) || !level.getWorldBorder().isWithinBounds(pos)) return Double.MAX_VALUE;
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block.defaultDestroyTime() < 0 || block.getExplosionResistance() >= 3600000 || block == Blocks.VOID_AIR || state.hasBlockEntity()) {
            return Double.MAX_VALUE;
        }
        double cost = 0;
        if (!state.getFluidState().isEmpty()) {
            cost += state.getFluidState().is(FluidTags.WATER) ? 1.5 : 5;
        }
        if (!state.isAir()) cost += state.canBeReplaced() ? 0.5 : 5;
        if (level.getBlockState(pos.below()).canBeReplaced()) cost += 1.5;
        return cost + pos.distSqr(origin);
    }

    public static void collect(ServerLevel level, Player player, BlockPos pos) {
        if (player instanceof FakePlayer || player.isDeadOrDying() || player.isSpectator()) return;
        if (!(level.getBlockEntity(pos) instanceof GraveBlockEntity grave)) return;

        int stage = grave.getBlockState().getValue(GraveBlock.DAMAGE);
        Inventory inventory = player.getInventory();
        for (GraveBlockEntity.Entry entry : grave.takeItems()) {
            ItemStack stack = entry.stack();
            int slot = entry.slot();
            if (slot >= 0 && slot < inventory.getContainerSize() && inventory.getItem(slot).isEmpty()) {
                inventory.setItem(slot, stack);
                continue;
            }
            inventory.add(stack);
            if (stack.isEmpty()) continue;
            ItemEntity dropped = player.drop(stack, false);
            if (dropped != null) {
                dropped.setNoPickUpDelay();
                dropped.setTarget(player.getUUID());
            }
        }
        int experience = decayedExperience(grave.takeExperience(), stage);
        if (experience > 0) player.giveExperiencePoints(experience);
        level.destroyBlock(pos, false);
    }

    public static void spill(ServerLevel level, BlockPos pos, GraveBlockEntity grave) {
        int stage = grave.getBlockState().getValue(GraveBlock.DAMAGE);
        for (GraveBlockEntity.Entry entry : grave.takeItems()) {
            ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, entry.stack());
            item.setUnlimitedLifetime();
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
        }
        int experience = decayedExperience(grave.takeExperience(), stage);
        if (experience > 0) ExperienceOrb.award(level, Vec3.atCenterOf(pos), experience);
        UUID owner = grave.getOwnerId();
        if (owner != null) GraveHistory.get(level.getServer()).remove(owner, GlobalPos.of(level.dimension(), pos));
    }

    private static int decayedExperience(int experience, int stage) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.gravestoneExperienceDecay, false)) {
            return experience / (stage + 1);
        }
        return experience;
    }

    public static void timeDecay(Level level, BlockPos pos, BlockState state, GraveBlockEntity grave) {
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.gravestoneDecayWithTime, true) || grave.getOwnerId() == null) return;
        long unit = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.gravestoneDecayHours, 8) * TICKS_PER_HOUR;
        long age = level.getGameTime() - grave.getCreatedTick();
        if (age > unit * 3) {
            grave.setAgeDamage(3);
        } else if (age > unit * 2) {
            grave.setAgeDamage(2);
        } else if (age > unit) {
            grave.setAgeDamage(1);
        }
        updateDamage(level, pos, state, grave);
    }

    public static void deathDecay(ServerPlayer player, @Nullable BlockPos newGrave) {
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.gravestoneDecayWithDeaths, true)) return;
        MinecraftServer server = player.getServer();
        if (server == null) return;
        GraveHistory history = GraveHistory.get(server);
        GlobalPos skip = newGrave == null ? null : GlobalPos.of(player.level().dimension(), newGrave);
        for (GlobalPos old : history.positions(player.getUUID())) {
            if (old.equals(skip)) continue;
            ServerLevel level = server.getLevel(old.dimension());
            if (level == null || !(level.getBlockEntity(old.pos()) instanceof GraveBlockEntity grave) || !player.getUUID().equals(grave.getOwnerId())) {
                history.remove(player.getUUID(), old);
                continue;
            }
            grave.addDeathDamage();
            updateDamage(level, old.pos(), level.getBlockState(old.pos()), grave);
        }
    }

    private static void updateDamage(Level level, BlockPos pos, BlockState state, GraveBlockEntity grave) {
        if (!state.hasProperty(GraveBlock.DAMAGE)) return;
        int stage = decayStage(grave.getTotalDamage());
        if (stage == state.getValue(GraveBlock.DAMAGE)) return;
        if (stage >= 3) {
            level.destroyBlock(pos, false);
        } else {
            level.setBlockAndUpdate(pos, state.setValue(GraveBlock.DAMAGE, stage));
        }
    }

    private static int decayStage(int totalDamage) {
        int damageToBreak = SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.gravestoneDamageToBreak, 3);
        if (totalDamage >= damageToBreak) return 3;
        if (totalDamage <= 0) return 0;
        if (totalDamage + 1 == damageToBreak) return 2;
        return 3 * totalDamage / damageToBreak;
    }
}

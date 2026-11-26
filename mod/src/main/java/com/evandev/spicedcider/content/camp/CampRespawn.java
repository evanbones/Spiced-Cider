package com.evandev.spicedcider.content.camp;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.networking.CampSpawnPayload;
import com.evandev.spicedcider.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class CampRespawn {
    private static final String VALHELSIA = "valhelsia_structures";
    private static final Map<UUID, CampSpawn> PENDING = new ConcurrentHashMap<>();

    private static boolean enabled() {
        return SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.tentRespawnChoice, false);
    }

    public static boolean isCamp(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id.getNamespace().equals(VALHELSIA) && (id.getPath().equals("explorers_tent") || id.getPath().endsWith("sleeping_bag"));
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (!enabled() || !(event.getEntity() instanceof ServerPlayer player) || !(event.getLevel() instanceof ServerLevel level)) return;
        if (isCamp(event.getPlacedBlock())) remember(player, level, event.getPos());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onSetSpawn(PlayerSetSpawnEvent event) {
        if (!enabled() || !(event.getEntity() instanceof ServerPlayer player)) return;
        BlockPos pos = event.getNewSpawn();
        if (pos == null) return;
        ServerLevel level = player.server.getLevel(event.getSpawnLevel());
        if (level == null || !isCamp(level.getBlockState(pos))) return;
        event.setCanceled(true);
        remember(player, level, pos);
    }

    private static void remember(ServerPlayer player, ServerLevel level, BlockPos pos) {
        CampSpawn previous = player.hasData(ModAttachments.CAMP_SPAWN) ? player.getData(ModAttachments.CAMP_SPAWN) : null;
        CampSpawn camp = new CampSpawn(level.dimension(), pos.immutable(), player.getYRot());
        player.setData(ModAttachments.CAMP_SPAWN, camp);
        if (previous == null || !previous.pos().equals(camp.pos()) || previous.dimension() != camp.dimension()) {
            player.displayClientMessage(Component.translatable("message.spicedcider.camp_spawn_set"), true);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) sync(player);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) sync(player);
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        PENDING.remove(event.getEntity().getUUID());
    }

    private static void sync(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new CampSpawnPayload(enabled(), player.getRespawnPosition() != null, validCamp(player).isPresent()));
    }

    private static Optional<CampSpawn> validCamp(ServerPlayer player) {
        if (!player.hasData(ModAttachments.CAMP_SPAWN)) return Optional.empty();
        CampSpawn camp = player.getData(ModAttachments.CAMP_SPAWN);
        ServerLevel level = player.server.getLevel(camp.dimension());
        if (level == null || !isCamp(level.getBlockState(camp.pos()))) return Optional.empty();
        return Optional.of(camp);
    }

    public static void choose(ServerPlayer player, boolean camp) {
        PENDING.remove(player.getUUID());
        if (!camp || !enabled() || player.isAlive() || !player.hasData(ModAttachments.CAMP_SPAWN)) return;
        PENDING.put(player.getUUID(), player.getData(ModAttachments.CAMP_SPAWN));
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        CampSpawn camp = PENDING.remove(player.getUUID());
        if (camp == null || event.isEndConquered()) return;
        ServerLevel level = player.server.getLevel(camp.dimension());
        Optional<Vec3> spot = level == null || !isCamp(level.getBlockState(camp.pos())) ? Optional.empty() : standingSpot(level, camp.pos());
        if (spot.isEmpty()) {
            player.removeData(ModAttachments.CAMP_SPAWN);
            player.displayClientMessage(Component.translatable("message.spicedcider.camp_spawn_missing"), false);
            return;
        }
        Vec3 pos = spot.get();
        player.teleportTo(level, pos.x, pos.y, pos.z, camp.angle(), 0.0F);
    }

    private static Optional<Vec3> standingSpot(ServerLevel level, BlockPos camp) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (int dy = 0; dy >= -1; dy--) {
                BlockPos feet = camp.relative(direction).above(dy);
                if (canStand(level, feet)) return Optional.of(Vec3.atBottomCenterOf(feet));
            }
        }
        for (int r = 2; r <= 3; r++) {
            for (BlockPos feet : BlockPos.betweenClosed(camp.offset(-r, -1, -r), camp.offset(r, 1, r))) {
                if (canStand(level, feet)) return Optional.of(Vec3.atBottomCenterOf(feet));
            }
        }
        return canStand(level, camp.above()) ? Optional.of(Vec3.atBottomCenterOf(camp.above())) : Optional.empty();
    }

    private static boolean canStand(ServerLevel level, BlockPos feet) {
        BlockState below = level.getBlockState(feet.below());
        return below.isFaceSturdy(level, feet.below(), Direction.UP)
                && level.getBlockState(feet).getCollisionShape(level, feet).isEmpty()
                && level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty()
                && level.getFluidState(feet).isEmpty()
                && level.getFluidState(feet.above()).isEmpty();
    }
}

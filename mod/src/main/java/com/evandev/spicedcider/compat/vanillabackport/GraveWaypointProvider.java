package com.evandev.spicedcider.compat.vanillabackport;

import com.blackgear.vanillabackport.client.api.modules.waypoints.ClientWaypointManager;
import com.blackgear.vanillabackport.client.api.modules.waypoints.provider.WaypointProvider;
import com.blackgear.vanillabackport.common.api.modules.waypoints.TrackedWaypoint;
import com.blackgear.vanillabackport.common.api.modules.waypoints.TrackedWaypointManager;
import com.blackgear.vanillabackport.common.api.modules.waypoints.Waypoint;
import com.blackgear.vanillabackport.common.api.modules.waypoints.WaypointStyleAssets;
import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.client.gravestone.ClientGraves;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.mojang.datafixers.util.Either;
import io.netty.buffer.Unpooled;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GraveWaypointProvider implements WaypointProvider {
    private static final ResourceKey<com.blackgear.vanillabackport.common.api.modules.waypoints.WaypointStyleAsset> STYLE =
            ResourceKey.create(WaypointStyleAssets.ROOT_ID, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "grave"));

    private static boolean registered;

    private final Map<Either<UUID, String>, TrackedWaypoint> tracked = new HashMap<>();

    public static void register() {
        if (registered) return;
        registered = true;
        ClientWaypointManager.INSTANCE.registerProvider(new GraveWaypointProvider());
    }

    @Override
    public void tick(LocalPlayer player, TrackedWaypointManager manager) {
        Map<Either<UUID, String>, TrackedWaypoint> current = new HashMap<>();
        if (SpicedCiderConfig.CLIENT.graveLocatorBar.get()) {
            for (GlobalPos pos : ClientGraves.positions()) {
                if (pos.dimension() != player.level().dimension()) continue;
                Either<UUID, String> id = Either.right("spicedcider_grave_" + pos.pos().asLong());
                current.put(id, waypoint(id, pos));
            }
        }

        this.tracked.keySet().removeIf(id -> {
            if (current.containsKey(id)) return false;
            manager.untrackWaypoint(this.tracked.get(id));
            return true;
        });

        Map<Either<UUID, String>, TrackedWaypoint> live = ClientWaypointManager.INSTANCE.waypoints();
        current.forEach((id, waypoint) -> {
            this.tracked.put(id, waypoint);
            if (!live.containsKey(id)) manager.trackWaypoint(waypoint);
        });
    }

    private static TrackedWaypoint waypoint(Either<UUID, String> id, GlobalPos pos) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(pos.pos().getX());
        buf.writeVarInt(pos.pos().getY());
        buf.writeVarInt(pos.pos().getZ());
        return new TrackedWaypoint.Vec3iWaypoint(id, new Waypoint.Icon(STYLE, Optional.of(CommonColors.WHITE)), buf);
    }
}

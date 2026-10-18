package com.evandev.spicedcider.compat.voxyworldgen;

import com.ethan.voxyworldgenv2.integration.VoxyIntegration;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public final class VoxyWorldGenHostIngest {
    private static final ThreadLocal<Boolean> SERVER_INGESTED = ThreadLocal.withInitial(() -> false);

    public static void begin() {
        SERVER_INGESTED.set(true);
    }

    public static void end() {
        SERVER_INGESTED.set(false);
    }

    public static List<ServerPlayer> withoutHost(List<ServerPlayer> recipients) {
        if (!SERVER_INGESTED.get() || !VoxyIntegration.isVoxyAvailable()) return recipients;
        List<ServerPlayer> remaining = null;
        for (int i = 0; i < recipients.size(); i++) {
            ServerPlayer player = recipients.get(i);
            if (isHost(player)) {
                if (remaining == null) remaining = new ArrayList<>(recipients.subList(0, i));
            } else if (remaining != null) {
                remaining.add(player);
            }
        }
        return remaining == null ? recipients : remaining;
    }

    private static boolean isHost(ServerPlayer player) {
        return !player.server.isDedicatedServer() && player.server.isSingleplayerOwner(player.getGameProfile());
    }
}

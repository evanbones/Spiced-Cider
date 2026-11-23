package com.evandev.spicedcider.compat.getoverit;

import com.evandev.spicedcider.networking.LedgeClimbPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.neoforge.network.PacketDistributor;

public class LedgeClimbNet {

    static void sendClimb() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null && connection.hasChannel(LedgeClimbPayload.TYPE)) {
            PacketDistributor.sendToServer(LedgeClimbPayload.INSTANCE);
        }
    }
}

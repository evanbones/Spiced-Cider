package com.evandev.spicedcider.client.gravestone;

import net.minecraft.core.GlobalPos;

import java.util.List;

public class ClientGraves {
    private static List<GlobalPos> positions = List.of();

    public static void receive(List<GlobalPos> received) {
        positions = List.copyOf(received);
    }

    public static List<GlobalPos> positions() {
        return positions;
    }
}

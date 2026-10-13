package com.evandev.spicedcider.mixin.fancymenu;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import de.keksuccino.fancymenu.networking.Packet;
import de.keksuccino.fancymenu.networking.PacketHandler;
import de.keksuccino.fancymenu.networking.packets.entities.EntityEventPacket;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("fancymenu")
@Mixin(value = PacketHandler.class, remap = false)
public class PacketHandlerEntityEventMixin {

    @WrapMethod(method = "sendToAllFancyMenuClients")
    private static <T extends Packet> void spicedcider$skipEntityEvents(MinecraftServer server, T packet, Operation<Void> original) {
        if (packet instanceof EntityEventPacket) return;
        original.call(server, packet);
    }
}

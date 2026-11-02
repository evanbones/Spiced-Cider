package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(LocalMobCapCalculator.class)
public interface LocalMobCapCalculatorAccessor {

    @Invoker("getPlayersNear")
    List<ServerPlayer> spicedcider$getPlayersNear(ChunkPos pos);
}

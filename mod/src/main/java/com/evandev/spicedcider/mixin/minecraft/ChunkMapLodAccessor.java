package com.evandev.spicedcider.mixin.minecraft;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapLodAccessor {

    @Invoker("upgradeChunkTag")
    CompoundTag spicedcider$upgradeChunkTag(CompoundTag tag);
}

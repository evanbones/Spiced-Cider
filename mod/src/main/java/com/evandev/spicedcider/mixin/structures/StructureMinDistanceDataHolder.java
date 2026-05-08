package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.structures.IGeneratorNearbyStructureHolder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.ConcurrentHashMap;

@Mixin({ChunkGenerator.class})
public class StructureMinDistanceDataHolder implements IGeneratorNearbyStructureHolder {
    @Unique
    private ConcurrentHashMap<Long, String> nearbyStructures = new ConcurrentHashMap<>();

    public String getNearby(long pos) {
        return this.nearbyStructures.get(pos);
    }

    public void setNearby(long pos, String name) {
        this.nearbyStructures.putIfAbsent(pos, name);
    }
}

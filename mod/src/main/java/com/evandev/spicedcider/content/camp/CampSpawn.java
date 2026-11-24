package com.evandev.spicedcider.content.camp;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record CampSpawn(ResourceKey<Level> dimension, BlockPos pos, float angle) {
    public static final CampSpawn NONE = new CampSpawn(Level.OVERWORLD, BlockPos.ZERO, 0.0F);

    public static final Codec<CampSpawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(CampSpawn::dimension),
            BlockPos.CODEC.fieldOf("pos").forGetter(CampSpawn::pos),
            Codec.FLOAT.optionalFieldOf("angle", 0.0F).forGetter(CampSpawn::angle)
    ).apply(instance, CampSpawn::new));
}

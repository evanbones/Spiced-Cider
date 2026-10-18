package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.compat.nomansland.OreVeinFiller;
import com.farcr.nomansland.common.world.orevein.OreVeinSystem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@IfModLoaded(value = "nomansland", minVersion = "1.5.12", maxVersion = "1.6.1")
@Mixin(value = OreVeinSystem.class, remap = false)
public class OreVeinFillMixin {

    @WrapMethod(method = "fill")
    private static void spicedcider$fillLazily(List<OreVeinSystem.OreVeinInstance> veins, WorldGenLevel level, ChunkAccess chunk, RandomState randomState, Operation<Void> original) {
        OreVeinFiller.fill(veins, level, chunk, randomState);
    }
}

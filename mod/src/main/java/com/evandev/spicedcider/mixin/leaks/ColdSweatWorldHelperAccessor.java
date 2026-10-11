package com.evandev.spicedcider.mixin.leaks;

import com.mojang.datafixers.util.Pair;
import com.momosoftworks.coldsweat.util.entity.DummyEntity;
import com.momosoftworks.coldsweat.util.entity.DummyPlayer;
import com.momosoftworks.coldsweat.util.world.WorldHelper;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@IfModLoaded("cold_sweat")
@Mixin(value = WorldHelper.class, remap = false)
public interface ColdSweatWorldHelperAccessor {
    @Accessor("DUMMY_PLAYERS")
    static Map<ResourceKey<Level>, DummyPlayer> spicedcider$dummyPlayers() {
        throw new AssertionError();
    }

    @Accessor("DUMMY_ENTITIES")
    static Map<ResourceKey<Level>, DummyEntity> spicedcider$dummyEntities() {
        throw new AssertionError();
    }

    @Accessor("TEMPERATURE_CACHE")
    static Map<ResourceKey<Level>, Map<BlockPos, ?>> spicedcider$temperatureCache() {
        throw new AssertionError();
    }

    @Accessor("INSULATION_CACHE")
    static Map<ResourceKey<Level>, Map<BlockPos, ?>> spicedcider$insulationCache() {
        throw new AssertionError();
    }

    @Accessor("BLOCK_ENTITY_DATA_CACHE")
    static Map<BlockEntity, Pair<CompoundTag, Long>> spicedcider$blockEntityDataCache() {
        throw new AssertionError();
    }
}

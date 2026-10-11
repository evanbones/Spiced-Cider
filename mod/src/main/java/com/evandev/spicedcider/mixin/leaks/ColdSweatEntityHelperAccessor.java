package com.evandev.spicedcider.mixin.leaks;

import com.mojang.datafixers.util.Pair;
import com.momosoftworks.coldsweat.util.entity.EntityHelper;
import com.momosoftworks.coldsweat.util.math.MappedCache;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("cold_sweat")
@Mixin(value = EntityHelper.class, remap = false)
public interface ColdSweatEntityHelperAccessor {
    @Accessor("ENTITY_DATA_CACHE")
    static MappedCache<Entity, Pair<CompoundTag, Long>> spicedcider$entityDataCache() {
        throw new AssertionError();
    }
}

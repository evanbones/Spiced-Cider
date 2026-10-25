package com.evandev.spicedcider.mixin.voxyworldgen;

import com.ethan.voxyworldgenv2.core.ChunkGenerationManager;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@IfModLoaded("voxyworldgenv2")
@Mixin(value = ChunkGenerationManager.class, remap = false)
public interface ChunkGenerationManagerAccessor {

    @Accessor("dimensionStates")
    Map<ResourceKey<Level>, ?> getDimensionStates();
}

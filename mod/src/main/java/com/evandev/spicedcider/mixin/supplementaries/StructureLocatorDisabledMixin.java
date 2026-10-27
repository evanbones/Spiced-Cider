package com.evandev.spicedcider.mixin.supplementaries;

import com.evandev.spicedcider.registry.ModTags;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.supplementaries.common.worldgen.StructureLocator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@IfModLoaded("supplementaries")
@Mixin(value = StructureLocator.class, remap = false)
public class StructureLocatorDisabledMixin {

    @WrapMethod(method = "findNearestStructures(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/HolderSet;Lnet/minecraft/core/BlockPos;IZIIZ)Ljava/util/List;")
    private static List<?> spicedcider$enabledOnly(ServerLevel level, HolderSet<Structure> targets, BlockPos center, int radius, boolean newlyGeneratedOnly,
                                                  int requiredCount, int maxStructures, boolean stopWhenFound, Operation<List<?>> original) {
        if (targets == null || targets.stream().noneMatch(holder -> holder.is(ModTags.Structures.DISABLED))) {
            return original.call(level, targets, center, radius, newlyGeneratedOnly, requiredCount, maxStructures, stopWhenFound);
        }
        List<Holder<Structure>> enabled = targets.stream().filter(holder -> !holder.is(ModTags.Structures.DISABLED)).toList();
        if (enabled.isEmpty()) return List.of();
        return original.call(level, HolderSet.direct(enabled), center, radius, newlyGeneratedOnly, requiredCount, maxStructures, stopWhenFound);
    }
}

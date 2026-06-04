package com.evandev.spicedcider.mixin.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin({JigsawPlacement.class})
public class JigsawPlacementMixin {
    @Inject(method = {"addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;ILnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/pools/DimensionPadding;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)Ljava/util/Optional;"}, at = {@At("HEAD")}, cancellable = true)
    private static void cider$addPieces(Structure.GenerationContext p_227239_, Holder<StructureTemplatePool> holder, Optional<ResourceLocation> p_227241_, int p_227242_, BlockPos p_227243_, boolean p_227244_, Optional<Heightmap.Types> p_227245_, int p_227246_, PoolAliasLookup p_307522_, DimensionPadding p_348489_, LiquidSettings p_352161_, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if (!holder.isBound())
            cir.setReturnValue(Optional.empty());
    }
}

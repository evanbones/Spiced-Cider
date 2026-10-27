package com.evandev.spicedcider.mixin.supplementaries;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("supplementaries")
@Mixin(targets = "net.mehvahdjukaar.supplementaries.common.worldgen.StructureLocator$StructureAndPlacement", remap = false)
public interface StructureAndPlacementAccessor {

    @Accessor("structure")
    Holder<Structure> spicedcider$structure();

    @Accessor("placement")
    RandomSpreadStructurePlacement spicedcider$placement();
}

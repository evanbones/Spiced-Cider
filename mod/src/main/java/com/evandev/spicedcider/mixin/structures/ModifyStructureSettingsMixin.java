package com.evandev.spicedcider.mixin.structures;

import com.evandev.spicedcider.structures.IStructureModifier;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin({ModifiableStructureInfo.class})
public class ModifyStructureSettingsMixin implements IStructureModifier {
    @Shadow
    @Nullable
    private ModifiableStructureInfo.StructureInfo modifiedStructureInfo;
    @Shadow
    @Final
    private ModifiableStructureInfo.StructureInfo originalStructureInfo;

    public void setStructureBiomes(HolderSet<Biome> newBiomes) {
        Structure.StructureSettings prev = (this.modifiedStructureInfo == null) ? this.originalStructureInfo.structureSettings() : this.modifiedStructureInfo.structureSettings();

        ModifiableStructureInfo.StructureInfo info = new ModifiableStructureInfo.StructureInfo(new Structure.StructureSettings(newBiomes, prev.spawnOverrides(), prev.step(), prev.terrainAdaptation()));
        this.modifiedStructureInfo = info;
    }
}


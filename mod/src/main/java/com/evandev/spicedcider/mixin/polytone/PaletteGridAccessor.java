package com.evandev.spicedcider.mixin.polytone;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.polytone.content.shaders.voxel.CellPalette;
import net.mehvahdjukaar.polytone.content.shaders.voxel.PaletteGrid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfModLoaded("polytone")
@Mixin(value = PaletteGrid.class, remap = false)
public interface PaletteGridAccessor {

    @Accessor("palette")
    CellPalette spicedcider$palette();
}

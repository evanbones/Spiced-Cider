package com.evandev.spicedcider.mixin.offgrid;

import com.evandev.spicedcider.compat.valhelsia.TentRugs;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.stal111.valhelsia_structures.common.block.ExplorersTentBlock;
import dev.tazer.offgrid.world.Layers;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("offgrid")
@IfModLoaded("valhelsia_structures")
@Mixin(value = Layers.class, remap = false)
public class LayersTentFloorMixin {
    @ModifyReturnValue(method = "cutsLayers", at = @At("RETURN"))
    private static boolean spicedcider$tentsKeepLayers(boolean cuts, BlockState placing) {
        return cuts || TentRugs.enabled() && placing.getBlock() instanceof ExplorersTentBlock;
    }
}

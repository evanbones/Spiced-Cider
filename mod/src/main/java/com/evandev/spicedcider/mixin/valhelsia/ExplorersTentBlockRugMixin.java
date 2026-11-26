package com.evandev.spicedcider.mixin.valhelsia;

import com.evandev.spicedcider.compat.valhelsia.TentRugs;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.stal111.valhelsia_structures.common.block.ExplorersTentBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("valhelsia_structures")
@Mixin(ExplorersTentBlock.class)
public class ExplorersTentBlockRugMixin {
    @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
    private VoxelShape spicedcider$clearFloor(VoxelShape shape) {
        return TentRugs.enabled() ? TentRugs.SHAPE : shape;
    }
}

package com.evandev.spicedcider.mixin.tide;

import com.evandev.spicedcider.compat.reliableremover.ReliableRemoverCompat;
import com.li64.tide.data.fishing.CrateData;
import com.li64.tide.data.fishing.FishingContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@IfModLoaded("tide")
@Mixin(value = CrateData.class, remap = false)
public class CrateDataMixin {

    @WrapMethod(method = "shouldKeep")
    private boolean spicedcider$skipRemovedCrates(FishingContext context, Operation<Boolean> original) {
        CrateData crate = (CrateData) (Object) this;
        if (ReliableRemoverCompat.isRemoved(crate.blockProvider().getState(context.rng(), BlockPos.ZERO).getBlock().asItem())) return false;
        return original.call(context);
    }
}

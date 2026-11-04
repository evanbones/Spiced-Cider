package com.evandev.spicedcider.mixin.manuallabour;

import com.evandev.spicedcider.util.BrokenItemUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@IfModLoaded("manual_labour")
@Mixin(targets = "com.evandev.manual_labour.content.block.entity.WorkstoneBlockEntity", remap = false)
public class WorkstoneBrokenToolMixin {
    @WrapMethod(method = "getMatchingProcess")
    private Optional<?> spicedcider$brokenToolCantWork(ItemStack tool, Operation<Optional<?>> original) {
        return BrokenItemUtil.isBroken(tool) ? Optional.empty() : original.call(tool);
    }
}

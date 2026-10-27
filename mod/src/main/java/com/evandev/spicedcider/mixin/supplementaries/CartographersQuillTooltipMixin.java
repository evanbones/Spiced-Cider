package com.evandev.spicedcider.mixin.supplementaries;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.mehvahdjukaar.supplementaries.integration.quark.CartographersQuillItem;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@IfModLoaded("supplementaries")
@Mixin(value = CartographersQuillItem.class, remap = false)
public class CartographersQuillTooltipMixin {

    @WrapOperation(method = "appendHoverText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
    private boolean spicedcider$targetIdAdvancedOnly(List<Object> tooltip, Object line, Operation<Boolean> original, @Local(argsOnly = true) TooltipFlag flag) {
        return flag.isAdvanced() && original.call(tooltip, line);
    }
}

package com.evandev.spicedcider.mixin.inlinetooltips;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("inline_tooltips")
@Mixin(targets = "cc.cassian.inline_tooltips.InlineTooltips", remap = false)
public class MultiplierAttributePercentMixin {

    @WrapOperation(method = "lambda$addAttributeTooltips$0", at = @At(value = "INVOKE", target = "Lcc/cassian/inline_tooltips/helpers/ModHelpers;format(DLnet/minecraft/core/Holder;)Ljava/lang/String;"))
    private static String spicedcider$multiplierAsPercent(double amount, Holder<Attribute> attribute, Operation<String> original, @Local(argsOnly = true) AttributeModifier modifier) {
        if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) return original.call(amount, attribute);
        return (modifier.amount() > 0 ? "+" : "") + ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(modifier.amount() * 100.0) + "%";
    }
}

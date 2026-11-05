package com.evandev.spicedcider.mixin.farmersdelight;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.util.BrokenItemUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("farmersdelight")
@Mixin(targets = "vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity")
public class CuttingBoardBrokenToolMixin {
    @WrapOperation(method = "getMatchingRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;test(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean spicedcider$brokenToolCantCut(Ingredient ingredient, ItemStack tool, Operation<Boolean> original) {
        if (BrokenItemUtil.isBroken(tool) && !SpicedCiderConfig.COMMON.brokenToolsOnCuttingBoards.get()) return false;
        return original.call(ingredient, tool);
    }
}

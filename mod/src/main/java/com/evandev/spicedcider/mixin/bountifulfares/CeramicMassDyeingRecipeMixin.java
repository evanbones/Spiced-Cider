package com.evandev.spicedcider.mixin.bountifulfares;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.hecco.bountifulfares.definition.item.custom.CeramicDishBlockItem;
import net.hecco.bountifulfares.definition.item.custom.DyeableCeramicBlockItem;
import net.hecco.bountifulfares.definition.recipe.CeramicMassDyeingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("bountifulfares")
@Mixin(value = CeramicMassDyeingRecipe.class, remap = false)
public class CeramicMassDyeingRecipeMixin {

    @ModifyReturnValue(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at = @At("RETURN"))
    private boolean spicedcider$requireCeramic(boolean original, CraftingInput input, Level level) {
        if (!original) return false;
        for (ItemStack stack : input.items()) {
            if (stack.getItem() instanceof DyeableCeramicBlockItem || stack.getItem() instanceof CeramicDishBlockItem) return true;
        }
        return false;
    }
}

package com.evandev.spicedcider.mixin.slime_time;

import cc.cassian.slime.api.SlimeColor;
import cc.cassian.slime.recipe.SlimeShapedRecipe;
import cc.cassian.slime.registry.SlimeBlocks;
import cc.cassian.slime.registry.SlimeDataComponents;
import cc.cassian.slime.tags.SlimeItemTags;
import cc.cassian.slime.util.SlimeHelpers;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@IfModLoaded("slime_time")
@Mixin(SlimeShapedRecipe.class)
public abstract class SlimeShapedRecipeColorMixin {

    @Shadow
    @Final
    public ItemStack result;

    @WrapMethod(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;")
    private ItemStack spicedcider$colorFromSlimeIngredients(CraftingInput input, HolderLookup.Provider registries, Operation<ItemStack> original) {
        SlimeColor color = null;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) continue;
            SlimeColor stackColor = spicedcider$slimeColor(stack);
            if (stackColor == null) continue;
            if (color == null) {
                color = stackColor;
            } else if (color != stackColor) {
                return original.call(input, registries);
            }
        }
        if (color == null) return original.call(input, registries);
        return SlimeHelpers.dye(result.copy(), color);
    }

    private static SlimeColor spicedcider$slimeColor(ItemStack stack) {
        Object dyed = stack.get(SlimeDataComponents.DYED_COLOR);
        if (dyed instanceof SlimeColor slimeColor) return slimeColor;
        if (stack.getItem() instanceof BlockItem blockItem) {
            for (var entry : SlimeBlocks.SLIME_BLOCKS.entrySet()) {
                if (entry.getValue() == blockItem.getBlock()) return entry.getKey();
            }
        }
        if (stack.is(SlimeItemTags.DYEABLE_SLIME) || stack.is(SlimeItemTags.SLIME_BLOCKS)) {
            for (SlimeColor slimeColor : SlimeColor.values()) {
                if (SlimeColor.isDefault(slimeColor)) return slimeColor;
            }
        }
        return null;
    }
}

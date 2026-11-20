package com.evandev.spicedcider.mixin.slime_time;

import cc.cassian.slime.recipe.SlimeShapedRecipe;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@IfModLoaded("slime_time")
@Mixin(SlimeShapedRecipe.class)
public abstract class SlimeShapedRecipeResultMixin extends CustomRecipe {

    @Shadow
    @Final
    public ItemStack result;

    public SlimeShapedRecipeResultMixin(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }
}

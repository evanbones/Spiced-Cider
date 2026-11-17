package com.evandev.spicedcider.mixin.spelunkery;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("spelunkery")
@IfModLoaded("emi")
@Mixin(targets = "com.ordana.spelunkery.integrations.EmiIntegration", remap = false)
public class EmiIntegrationGrindstoneMixin {

    @Unique
    private static final ResourceLocation spicedcider$DIAMOND_GRINDSTONE = ResourceLocation.fromNamespaceAndPath("spelunkery", "diamond_grindstone");

    @WrapOperation(
            method = "register",
            at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/recipe/EmiWorldInteractionRecipe$Builder;rightInput(Ldev/emi/emi/api/stack/EmiIngredient;Z)Ldev/emi/emi/api/recipe/EmiWorldInteractionRecipe$Builder;")
    )
    private EmiWorldInteractionRecipe.Builder spicedcider$plainGrindstone(EmiWorldInteractionRecipe.Builder builder, EmiIngredient ingredient, boolean catalyst, Operation<EmiWorldInteractionRecipe.Builder> original) {
        if (SpicedCiderConfig.clientOr(SpicedCiderConfig.CLIENT.roughGemsOnGrindstone, false)
                && ingredient.getEmiStacks().size() == 1
                && spicedcider$DIAMOND_GRINDSTONE.equals(BuiltInRegistries.ITEM.getKey(ingredient.getEmiStacks().getFirst().getItemStack().getItem()))) {
            return original.call(builder, EmiStack.of(Blocks.GRINDSTONE), catalyst);
        }
        return original.call(builder, ingredient, catalyst);
    }
}

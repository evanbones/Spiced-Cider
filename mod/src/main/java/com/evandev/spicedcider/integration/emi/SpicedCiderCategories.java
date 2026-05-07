package com.evandev.spicedcider.integration.emi;

import com.evandev.spicedcider.SpicedCider;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.resources.ResourceLocation;

public class SpicedCiderCategories {
    private static final ResourceLocation SIMPLIFIED_TEXTURES = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "textures/gui/emi/simplified.png");

    public static final EmiRecipeCategory WORKSTONE = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "workstone"),
            SpicedCiderWorkstations.WORKSTONE,
            simplifiedRenderer(0, 0)
    );

    private static EmiRenderable simplifiedRenderer(int u, int v) {
        return (draw, x, y, delta) -> draw.blit(SIMPLIFIED_TEXTURES, x, y, u, v, 16, 16, 16, 16);
    }
}
package com.evandev.spicedcider.mixin.emi;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@IfModLoaded("emi")
@Mixin(value = EmiRegistryImpl.class, remap = false)
public class EmiRegistryHauntingMixin {
    private static final ResourceLocation SPICEDCIDER$HAUNTING = ResourceLocation.fromNamespaceAndPath("create", "fan_haunting");
    private static final Set<ResourceLocation> SPICEDCIDER$FAN_ONLY = Set.of(
            ResourceLocation.fromNamespaceAndPath("create", "fan_washing"),
            ResourceLocation.fromNamespaceAndPath("create", "fan_smoking"),
            ResourceLocation.fromNamespaceAndPath("create", "fan_blasting")
    );
    private static final ResourceLocation SPICEDCIDER$SOUL_FIRE = ResourceLocation.withDefaultNamespace("block/soul_fire_0");

    @Inject(method = "addCategory", at = @At("TAIL"))
    private void spicedcider$soulHauntingCategory(EmiRecipeCategory category, CallbackInfo ci) {
        if (!SPICEDCIDER$HAUNTING.equals(category.getId())) return;
        EmiRenderable soulFire = (graphics, x, y, delta) -> {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SPICEDCIDER$SOUL_FIRE);
            graphics.blit(x, y, 0, 16, 16, sprite);
        };
        category.icon = soulFire;
        category.simplified = soulFire;
        ((EmiRegistry) this).addWorkstation(category, EmiStack.of(Items.SOUL_CAMPFIRE));
    }

    @Inject(method = "addRecipe", at = @At("HEAD"), cancellable = true)
    private void spicedcider$dropFanOnlyRecipes(EmiRecipe recipe, CallbackInfo ci) {
        EmiRecipeCategory category = recipe.getCategory();
        if (category != null && SPICEDCIDER$FAN_ONLY.contains(category.getId())) ci.cancel();
    }
}

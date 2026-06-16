package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.recipe.ShapelessRepairRecipe;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerRepairMixin {

    @Shadow
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Inject(
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL")
    )
    private void cider$addRepairRecipes(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        List<RecipeHolder<ShapelessRepairRecipe>> generated = ShapelessRepairRecipe.generateAll();
        if (generated.isEmpty()) return;

        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> typeBuilder = ImmutableMultimap.builder();
        typeBuilder.putAll(byType);

        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> nameBuilder = ImmutableMap.builder();
        nameBuilder.putAll(byName);

        int added = 0;
        for (RecipeHolder<ShapelessRepairRecipe> holder : generated) {
            if (!byName.containsKey(holder.id())) {
                typeBuilder.put(RecipeType.CRAFTING, holder);
                nameBuilder.put(holder.id(), holder);
                added++;
            }
        }

        byType = typeBuilder.build();
        byName = nameBuilder.build();

        SpicedCider.LOGGER.info("Injected {} inventory repair recipes.", added);
    }
}

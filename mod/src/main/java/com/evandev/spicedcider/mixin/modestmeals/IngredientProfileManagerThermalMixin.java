package com.evandev.spicedcider.mixin.modestmeals;

import com.evandev.spicedcider.compat.modestmeals.ThermalEffectRemap;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@IfModLoaded("modest_meals")
@Mixin(targets = "com.evandev.modest_meals.food.ingredient.IngredientProfileManager", remap = false)
public class IngredientProfileManagerThermalMixin {

    @WrapMethod(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V")
    private void spicedcider$remapThermalEffects(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler, Operation<Void> original) {
        original.call(ThermalEffectRemap.ingredients(resources), manager, profiler);
    }
}

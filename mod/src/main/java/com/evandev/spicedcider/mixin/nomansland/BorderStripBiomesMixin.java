package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.farcr.nomansland.common.world.generation.NMLBiomePlacements;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.terraformersmc.biolith.api.biome.sub.Criterion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("nomansland")
@Mixin(value = NMLBiomePlacements.class, remap = false)
public class BorderStripBiomesMixin {

    @WrapOperation(method = "transitionalBiome", at = @At(value = "INVOKE", target = "Lcom/terraformersmc/biolith/api/biome/BiomePlacement;addSubOverworld(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceKey;Lcom/terraformersmc/biolith/api/biome/sub/Criterion;)V"))
    private static void spicedcider$skipTransitionStrip(ResourceKey<Biome> target, ResourceKey<Biome> biome, Criterion criterion, Operation<Void> original) {
        if (!enabled()) original.call(target, biome, criterion);
    }

    @WrapOperation(method = "register", at = @At(value = "INVOKE", target = "Lcom/terraformersmc/biolith/api/biome/BiomePlacement;addSubOverworld(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceKey;Lcom/terraformersmc/biolith/api/biome/sub/Criterion;)V"))
    private static void spicedcider$skipBorderStrip(ResourceKey<Biome> target, ResourceKey<Biome> biome, Criterion criterion, Operation<Void> original) {
        if (enabled() && isBorderStrip(target, biome)) return;
        original.call(target, biome, criterion);
    }

    private static boolean enabled() {
        return SpicedCiderConfig.startupOr(SpicedCiderConfig.STARTUP.noBorderStripBiomes, false);
    }

    private static boolean isBorderStrip(ResourceKey<Biome> target, ResourceKey<Biome> biome) {
        String path = biome.location().getPath();
        String host = target.location().getPath();
        if (path.equals("bog")) return true;
        if (path.equals("maple_grove")) return host.equals("maple_forest") || host.equals("grove");
        return path.equals("maple_forest") && host.equals("forest");
    }
}

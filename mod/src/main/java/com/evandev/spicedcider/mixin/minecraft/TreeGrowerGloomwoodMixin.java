package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.registry.ModTreeFeatures;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TreeGrower.class)
public class TreeGrowerGloomwoodMixin {
    @Shadow
    @Final
    private String name;

    @ModifyReturnValue(method = "getConfiguredFeature", at = @At("RETURN"))
    private ResourceKey<ConfiguredFeature<?, ?>> spicedcider$gloomwoodTree(ResourceKey<ConfiguredFeature<?, ?>> original) {
        return "pale_oak".equals(this.name) ? ModTreeFeatures.GLOOMWOOD : original;
    }

    @ModifyReturnValue(method = "getConfiguredMegaFeature", at = @At("RETURN"))
    private ResourceKey<ConfiguredFeature<?, ?>> spicedcider$noMegaGloomwood(ResourceKey<ConfiguredFeature<?, ?>> original) {
        return "pale_oak".equals(this.name) ? null : original;
    }
}

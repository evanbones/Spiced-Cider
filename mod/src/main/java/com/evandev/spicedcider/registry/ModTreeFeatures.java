package com.evandev.spicedcider.registry;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.content.worldgen.BiomeClearancePlacement;
import com.evandev.spicedcider.content.worldgen.CaveRootFeature;
import com.evandev.spicedcider.content.worldgen.SpindlyTrunkPlacer;
import com.evandev.spicedcider.content.worldgen.StripeFeature;
import com.evandev.spicedcider.content.worldgen.TrunkCreakingHeartDecorator;
import com.evandev.spicedcider.content.worldgen.TrunkShootsDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTreeFeatures {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, SpicedCider.MOD_ID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, SpicedCider.MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, SpicedCider.MOD_ID);
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, SpicedCider.MOD_ID);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<SpindlyTrunkPlacer>> SPINDLY_TRUNK_PLACER = TRUNK_PLACERS.register("spindly_trunk_placer",
            () -> new TrunkPlacerType<>(SpindlyTrunkPlacer.CODEC));

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<TrunkCreakingHeartDecorator>> TRUNK_CREAKING_HEART = TREE_DECORATORS.register("trunk_creaking_heart",
            () -> new TreeDecoratorType<>(TrunkCreakingHeartDecorator.CODEC));

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<TrunkShootsDecorator>> TRUNK_SHOOTS = TREE_DECORATORS.register("trunk_shoots",
            () -> new TreeDecoratorType<>(TrunkShootsDecorator.CODEC));

    public static final DeferredHolder<Feature<?>, CaveRootFeature> CAVE_ROOT = FEATURES.register("cave_root", CaveRootFeature::new);
    public static final DeferredHolder<Feature<?>, StripeFeature> STRIPES = FEATURES.register("stripes", StripeFeature::new);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<BiomeClearancePlacement>> BIOME_CLEARANCE = PLACEMENT_MODIFIERS.register("biome_clearance",
            () -> () -> BiomeClearancePlacement.CODEC);

    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOAMWOOD = key("gloamwood");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLOOMWOOD = key("gloomwood");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, name));
    }
}

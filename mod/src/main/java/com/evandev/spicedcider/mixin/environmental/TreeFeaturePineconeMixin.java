package com.evandev.spicedcider.mixin.environmental;

import com.evandev.spicedcider.registry.ModTags;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.JsonOps;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;
import java.util.function.BiConsumer;

@IfModLoaded("environmental")
@Mixin(TreeFeature.class)
public class TreeFeaturePineconeMixin {
    @Unique
    private static TreeDecorator spicedcider$pinecones;

    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/TreeFeature;doPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Ljava/util/function/BiConsumer;Ljava/util/function/BiConsumer;Lnet/minecraft/world/level/levelgen/feature/foliageplacers/FoliagePlacer$FoliageSetter;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;)Z"))
    private boolean spicedcider$addPinecones(TreeFeature feature, WorldGenLevel level, RandomSource random, BlockPos pos,
                                             BiConsumer<BlockPos, BlockState> roots, BiConsumer<BlockPos, BlockState> trunk,
                                             FoliagePlacer.FoliageSetter foliage, TreeConfiguration config, Operation<Boolean> original) {
        Set<BlockPos> logs = Sets.newHashSet();
        Set<BlockPos> leaves = Sets.newHashSet();
        BiConsumer<BlockPos, BlockState> trackedTrunk = (p, state) -> {
            logs.add(p.immutable());
            trunk.accept(p, state);
        };
        FoliagePlacer.FoliageSetter trackedFoliage = new FoliagePlacer.FoliageSetter() {
            @Override
            public void set(BlockPos p, BlockState state) {
                leaves.add(p.immutable());
                foliage.set(p, state);
            }

            @Override
            public boolean isSet(BlockPos p) {
                return foliage.isSet(p);
            }
        };

        boolean placed = original.call(feature, level, random, pos, roots, trackedTrunk, trackedFoliage, config);
        if (!placed || logs.isEmpty() || leaves.isEmpty()) return placed;
        if (!level.getBlockState(logs.iterator().next()).is(ModTags.Blocks.GROWS_PINECONES)) return placed;

        TreeDecorator decorator = spicedcider$pinecones();
        if (decorator != null) {
            BiConsumer<BlockPos, BlockState> setter = (p, state) -> level.setBlock(p, state, 19);
            decorator.place(new TreeDecorator.Context(level, setter, random, logs, leaves, Sets.newHashSet()));
        }
        return placed;
    }

    @Unique
    private static TreeDecorator spicedcider$pinecones() {
        if (spicedcider$pinecones == null) {
            TreeDecoratorType<?> type = BuiltInRegistries.TREE_DECORATOR_TYPE.get(ResourceLocation.fromNamespaceAndPath("environmental", "pinecone"));
            if (type == null) return null;
            JsonObject json = new JsonObject();
            json.addProperty("probability", 0.05F);
            spicedcider$pinecones = type.codec().codec().parse(JsonOps.INSTANCE, json).result().orElse(null);
        }
        return spicedcider$pinecones;
    }
}

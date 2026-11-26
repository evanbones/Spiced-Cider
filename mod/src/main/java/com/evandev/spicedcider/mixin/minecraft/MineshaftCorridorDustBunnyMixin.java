package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("spelunkery")
@Mixin(MineshaftPieces.MineShaftCorridor.class)
public class MineshaftCorridorDustBunnyMixin {

    @Unique
    private static final ResourceLocation spicedcider$DUST_BUNNY = ResourceLocation.fromNamespaceAndPath("spelunkery", "dust_bunny");

    @WrapOperation(method = "postProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity;setEntityId(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/util/RandomSource;)V"))
    private void spicedcider$dustBunnySpawner(SpawnerBlockEntity spawner, EntityType<?> type, RandomSource random, Operation<Void> original) {
        if (SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.dustBunnySpawners, false) && random.nextBoolean()) {
            original.call(spawner, BuiltInRegistries.ENTITY_TYPE.getOptional(spicedcider$DUST_BUNNY).orElse(type), random);
            return;
        }
        original.call(spawner, type, random);
    }
}

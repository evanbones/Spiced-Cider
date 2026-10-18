package com.evandev.spicedcider.mixin.bettermineshafts;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("bettermineshafts")
@IfModLoaded("spelunkery")
@Mixin(targets = "com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SideRoomDungeon", remap = false)
public class SideRoomDungeonDustBunnyMixin {

    @Unique
    private static final ResourceLocation spicedcider$DUST_BUNNY = ResourceLocation.fromNamespaceAndPath("spelunkery", "dust_bunny");

    @WrapOperation(method = "postProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity;setEntityId(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/util/RandomSource;)V"))
    private void spicedcider$dustBunnySpawner(SpawnerBlockEntity spawner, EntityType<?> type, RandomSource random, Operation<Void> original) {
        if (random.nextBoolean()) {
            EntityType<?> dustBunny = BuiltInRegistries.ENTITY_TYPE.getOptional(spicedcider$DUST_BUNNY).orElse(type);
            original.call(spawner, dustBunny, random);
            return;
        }
        original.call(spawner, type, random);
    }
}

package com.evandev.spicedcider.mixin.neoforge;

import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = FluidType.class, remap = false)
public abstract class FluidTypeWaterLikeMixin {
    @Unique
    private List<Fluid> spicedcider$fluids;

    @Unique
    private boolean spicedcider$is(TagKey<Fluid> tag) {
        if (this.spicedcider$fluids == null) {
            FluidType self = (FluidType) (Object) this;
            this.spicedcider$fluids = BuiltInRegistries.FLUID.stream().filter(fluid -> fluid.getFluidType() == self).toList();
        }
        for (Fluid fluid : this.spicedcider$fluids) {
            if (fluid.is(tag)) return true;
        }
        return false;
    }

    @Inject(method = "canExtinguish(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$extinguishEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (spicedcider$is(ModTags.Fluids.EXTINGUISHES_FIRE)) cir.setReturnValue(true);
    }

    @Inject(method = "canExtinguish(Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$extinguishAt(FluidState state, BlockGetter getter, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(ModTags.Fluids.EXTINGUISHES_FIRE)) cir.setReturnValue(true);
    }

    @Inject(method = "canHydrate(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$hydrateEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (spicedcider$is(ModTags.Fluids.HYDRATES)) cir.setReturnValue(true);
    }

    @Inject(method = "canHydrate(Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void spicedcider$hydrateAt(FluidState state, BlockGetter getter, BlockPos pos, BlockState source, BlockPos sourcePos, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(ModTags.Fluids.HYDRATES)) cir.setReturnValue(true);
    }
}

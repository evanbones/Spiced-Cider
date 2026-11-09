package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.compat.nomansland.tap.TapFaucetBridge;
import com.evandev.spicedcider.compat.nomansland.tap.TapFluidHolder;
import com.evandev.spicedcider.compat.nomansland.tap.TapFluidState;
import com.farcr.nomansland.common.blockentity.TapBlockEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("nomansland")
@IfModLoaded("supplementaries")
@Mixin(value = TapBlockEntity.class, remap = false)
public class TapBlockEntityFaucetMixin implements TapFluidHolder {
    @Unique
    private static final String spicedcider$PUT_FLUID = "Lcom/farcr/nomansland/common/blockentity/TapBlockEntity;putFluid(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/neoforged/neoforge/fluids/FluidStack;)Z";
    @Unique
    private static final String spicedcider$RAISE_CAULDRON = "Lcom/farcr/nomansland/common/blockentity/TapBlockEntity;raiseCauldron(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/Block;)Z";

    @Unique
    private final TapFluidState spicedcider$fluids = new TapFluidState();

    @Override
    public TapFluidState spicedcider$fluids() {
        return spicedcider$fluids;
    }

    @WrapMethod(method = "tickServer")
    private void spicedcider$pourFromFaucetSources(ServerLevel level, BlockPos pos, BlockState state, Operation<Void> original) {
        original.call(level, pos, state);
        TapFaucetBridge.tick((TapBlockEntity) (Object) this, spicedcider$fluids, level, pos, level.getBlockState(pos));
    }

    @WrapOperation(method = "refresh", at = @At(value = "INVOKE", target = "Lcom/farcr/nomansland/common/blockentity/TapBlockEntity;findTarget(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;"))
    private BlockPos spicedcider$targetBelow(Level level, BlockPos tap, Operation<BlockPos> original) {
        return TapFaucetBridge.target(level, tap, original.call(level, tap));
    }

    @WrapOperation(method = "perform", at = @At(value = "INVOKE", target = spicedcider$PUT_FLUID, ordinal = 0))
    private boolean spicedcider$tappedIntoFaucetTarget(Level level, BlockPos target, BlockState state, FluidStack stack, Operation<Boolean> original) {
        FluidStack offered = stack.copy();
        return original.call(level, target, state, stack) || TapFaucetBridge.fillFallback(level, target, state, offered);
    }

    @WrapOperation(method = "perform", at = {
            @At(value = "INVOKE", target = spicedcider$PUT_FLUID, ordinal = 1),
            @At(value = "INVOKE", target = spicedcider$PUT_FLUID, ordinal = 2),
            @At(value = "INVOKE", target = spicedcider$PUT_FLUID, ordinal = 3)
    })
    private boolean spicedcider$pourIntoFaucetTarget(Level level, BlockPos target, BlockState state, FluidStack stack, Operation<Boolean> original) {
        FluidStack offered = stack.copy();
        boolean filled = original.call(level, target, state, stack) || TapFaucetBridge.fillFallback(level, target, state, offered);
        if (filled) {
            TapBlockEntity tap = (TapBlockEntity) (Object) this;
            TapFaucetBridge.showStream(tap, spicedcider$fluids, level, tap.getBlockPos(), target, offered.getFluid());
        }
        return filled;
    }

    @WrapOperation(method = "perform", at = @At(value = "INVOKE", target = spicedcider$RAISE_CAULDRON, ordinal = 0))
    private boolean spicedcider$tappedCauldronIntoFaucetTarget(Level level, BlockPos target, BlockState state, Block cauldron, Operation<Boolean> original) {
        return original.call(level, target, state, cauldron) || TapFaucetBridge.cauldronFallback(level, target, state, cauldron);
    }

    @WrapOperation(method = "perform", at = @At(value = "INVOKE", target = spicedcider$RAISE_CAULDRON, ordinal = 1))
    private boolean spicedcider$cauldronIntoFaucetTarget(Level level, BlockPos target, BlockState state, Block cauldron, Operation<Boolean> original) {
        boolean filled = original.call(level, target, state, cauldron) || TapFaucetBridge.cauldronFallback(level, target, state, cauldron);
        if (filled) {
            TapBlockEntity tap = (TapBlockEntity) (Object) this;
            TapFaucetBridge.showCauldronStream(tap, spicedcider$fluids, level, tap.getBlockPos(), target, cauldron);
        }
        return filled;
    }

    @ModifyReturnValue(method = "getUpdateTag", at = @At("RETURN"))
    private CompoundTag spicedcider$syncStream(CompoundTag tag, HolderLookup.Provider registries) {
        TapFaucetBridge.writeStream(spicedcider$fluids, tag, registries);
        return tag;
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void spicedcider$loadStream(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        TapFaucetBridge.readStream(spicedcider$fluids, tag, registries);
    }
}

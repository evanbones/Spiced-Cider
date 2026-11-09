package com.evandev.spicedcider.mixin.nomansland;

import com.evandev.spicedcider.compat.nomansland.tap.TapPowerMemory;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.farcr.nomansland.common.blockentity.TapBlockEntity;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("nomansland")
@Mixin(value = TapBlockEntity.class, remap = false)
public class TapBlockEntityPowerMixin implements TapPowerMemory {
    @Unique
    private static final String spicedcider$POWERED_KEY = "spicedcider:powered";

    @Unique
    @Nullable
    private Boolean spicedcider$powered;

    @Override
    public @Nullable Boolean spicedcider$powered() {
        return spicedcider$powered;
    }

    @Override
    public void spicedcider$setPowered(@Nullable Boolean powered) {
        if (powered == spicedcider$powered) return;
        spicedcider$powered = powered;
        ((TapBlockEntity) (Object) this).setChanged();
    }

    @Inject(method = "tickServer", at = @At("HEAD"))
    private void spicedcider$rememberInitialPower(ServerLevel level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (spicedcider$powered == null && SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.tapRedstone, false)) {
            spicedcider$setPowered(level.hasNeighborSignal(pos));
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void spicedcider$savePower(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (spicedcider$powered != null) tag.putBoolean(spicedcider$POWERED_KEY, spicedcider$powered);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void spicedcider$loadPower(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (tag.contains(spicedcider$POWERED_KEY)) spicedcider$powered = tag.getBoolean(spicedcider$POWERED_KEY);
    }
}

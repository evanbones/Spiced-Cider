package com.evandev.spicedcider.mixin.blockgrid;

import com.evandev.spicedcider.blockgrid.ClientOffsetCache;
import com.evandev.spicedcider.blockgrid.storage.SignOffsetHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityOffsetMixin extends BlockEntity implements SignOffsetHolder {
    @Unique
    private static final String spicedcider$KEY_X = "SpicedCiderSignOffsetX";
    @Unique
    private static final String spicedcider$KEY_Y = "SpicedCiderSignOffsetY";
    @Unique
    private static final String spicedcider$KEY_Z = "SpicedCiderSignOffsetZ";

    @Unique
    private Vec3 spicedcider$signOffset = Vec3.ZERO;

    public SignBlockEntityOffsetMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Vec3 spicedcider$getSignOffset() {
        return this.spicedcider$signOffset;
    }

    @Override
    public void spicedcider$setSignOffset(Vec3 offset) {
        this.spicedcider$signOffset = offset;
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void spicedcider$writeSignOffset(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.spicedcider$signOffset != Vec3.ZERO) {
            tag.putDouble(spicedcider$KEY_X, this.spicedcider$signOffset.x);
            tag.putDouble(spicedcider$KEY_Y, this.spicedcider$signOffset.y);
            tag.putDouble(spicedcider$KEY_Z, this.spicedcider$signOffset.z);
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void spicedcider$readSignOffset(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        Vec3 stored = tag.contains(spicedcider$KEY_X)
                ? new Vec3(tag.getDouble(spicedcider$KEY_X), tag.getDouble(spicedcider$KEY_Y), tag.getDouble(spicedcider$KEY_Z))
                : Vec3.ZERO;
        boolean moved = !stored.equals(this.spicedcider$signOffset);
        this.spicedcider$signOffset = stored;
        if (moved && this.level != null && this.level.isClientSide) {
            ClientOffsetCache.refreshSection(this.level, getBlockPos());
        }
    }
}

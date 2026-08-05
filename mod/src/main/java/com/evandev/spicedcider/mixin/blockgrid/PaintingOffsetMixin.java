package com.evandev.spicedcider.mixin.blockgrid;

import com.evandev.spicedcider.blockgrid.PackedSurfaceOffset;
import com.evandev.spicedcider.blockgrid.storage.SurfaceOffsetHolder;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Painting.class)
public abstract class PaintingOffsetMixin extends HangingEntity implements SurfaceOffsetHolder {
    @Unique
    private static final EntityDataAccessor<Vector3f> SPICEDCIDER$SURFACE_OFFSET = SynchedEntityData.defineId(Painting.class, EntityDataSerializers.VECTOR3);

    protected PaintingOffsetMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Vector3f spicedcider$getSurfaceOffset() {
        return getEntityData().get(SPICEDCIDER$SURFACE_OFFSET);
    }

    @Override
    public void spicedcider$setSurfaceOffset(Vector3f offset) {
        getEntityData().set(SPICEDCIDER$SURFACE_OFFSET, offset);
        setDirection(getDirection());
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void spicedcider$registerSurfaceOffsetData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(SPICEDCIDER$SURFACE_OFFSET, new Vector3f());
    }

    @ModifyReturnValue(method = "calculateBoundingBox", at = @At("RETURN"))
    private AABB spicedcider$offsetBoundingBox(AABB original, BlockPos pos, Direction direction) {
        Vector3f offset = getEntityData().get(SPICEDCIDER$SURFACE_OFFSET);
        return (offset.x == 0.0F && offset.y == 0.0F && offset.z == 0.0F) ? original : original.move(offset.x, offset.y, offset.z);
    }

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    private void spicedcider$reapplyDirectionOnSync(EntityDataAccessor<?> key, CallbackInfo ci) {
        if (SPICEDCIDER$SURFACE_OFFSET.equals(key) && getDirection() != null) {
            setDirection(getDirection());
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void spicedcider$writeSurfaceOffset(CompoundTag tag, CallbackInfo ci) {
        Vector3f offset = getEntityData().get(SPICEDCIDER$SURFACE_OFFSET);
        if (offset.x != 0.0F || offset.y != 0.0F || offset.z != 0.0F) {
            tag.putFloat("SpicedCiderSurfaceOffsetX", offset.x);
            tag.putFloat("SpicedCiderSurfaceOffsetY", offset.y);
            tag.putFloat("SpicedCiderSurfaceOffsetZ", offset.z);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void spicedcider$readSurfaceOffset(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("SpicedCiderSurfaceOffsetX")) {
            getEntityData().set(SPICEDCIDER$SURFACE_OFFSET, new Vector3f(
                    tag.getFloat("SpicedCiderSurfaceOffsetX"),
                    tag.getFloat("SpicedCiderSurfaceOffsetY"),
                    tag.getFloat("SpicedCiderSurfaceOffsetZ")
            ));
            setDirection(getDirection());
        }
    }

    @ModifyExpressionValue(method = "getAddEntityPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Direction;get3DDataValue()I"))
    private int spicedcider$encodeOffsetInSpawnData(int direction) {
        return PackedSurfaceOffset.pack(direction, getEntityData().get(SPICEDCIDER$SURFACE_OFFSET));
    }

    @ModifyExpressionValue(method = "recreateFromPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;getData()I"))
    private int spicedcider$decodeOffsetFromSpawnData(int packed) {
        getEntityData().set(SPICEDCIDER$SURFACE_OFFSET, PackedSurfaceOffset.unpack(packed));
        return PackedSurfaceOffset.face(packed);
    }
}

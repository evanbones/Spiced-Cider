package com.evandev.spicedcider.mixin.voidwater;

import com.evandev.spicedcider.voidwater.VoidTrailColumns;
import com.evandev.spicedcider.voidwater.VoidTrails;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract Level level();

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract void setDeltaMovement(Vec3 deltaMovement);

    @Shadow
    public abstract BlockPos blockPosition();

    @Inject(method = "updateFluidHeightAndDoFluidPushing()V", at = @At("RETURN"))
    private void spicedcider$pushDownVoidTrails(CallbackInfo ci) {
        Level level = level();
        VoidTrailColumns columns = VoidTrails.columns(level);
        if (columns == null) return;
        AABB box = getBoundingBox();
        int minY = level.getMinBuildHeight();
        if (box.maxY >= minY) return;

        Entity self = (Entity) (Object) this;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Set<FluidType> pushed = new HashSet<>();
        double push = 0.0;
        for (int x = Mth.floor(box.minX); x < Mth.ceil(box.maxX); x++) {
            for (int z = Mth.floor(box.minZ); z < Mth.ceil(box.maxZ); z++) {
                int length = columns.get(x, z);
                if (length == 0 || box.maxY <= minY - length) continue;
                FluidType type = level.getFluidState(pos.set(x, minY, z)).getFluidType();
                if (!type.isAir() && type.canPushEntity(self) && pushed.add(type)) {
                    push += self.getFluidMotionScale(type);
                }
            }
        }
        if (push != 0.0) {
            setDeltaMovement(getDeltaMovement().add(0.0, -push, 0.0));
        }
    }

    @Inject(method = "checkInsideBlocks", at = @At("RETURN"))
    private void spicedcider$touchVoidTrails(CallbackInfo ci) {
        Level level = level();
        VoidTrailColumns columns = VoidTrails.columns(level);
        if (columns == null) return;
        AABB box = getBoundingBox();
        int minY = level.getMinBuildHeight();
        if (box.maxY >= minY) return;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Set<FluidType> touched = new HashSet<>();
        for (int x = Mth.floor(box.minX); x < Mth.ceil(box.maxX); x++) {
            for (int z = Mth.floor(box.minZ); z < Mth.ceil(box.maxZ); z++) {
                int length = columns.get(x, z);
                if (length == 0 || box.maxY <= minY - length) continue;
                pos.set(x, minY, z);
                FluidState fluid = level.getFluidState(pos);
                if (!fluid.isEmpty() && touched.add(fluid.getFluidType())) {
                    level.getBlockState(pos).entityInside(level, pos.immutable(), (Entity) (Object) this);
                }
            }
        }
    }

    @ModifyVariable(method = "updateFluidOnEyes()V", at = @At("STORE"), ordinal = 0)
    private double spicedcider$eyesInVoidTrail(double eyeY) {
        Level level = level();
        int minY = level.getMinBuildHeight();
        if (eyeY >= minY) return eyeY;
        BlockPos pos = blockPosition();
        int length = VoidTrails.lengthAt(level, pos.getX(), pos.getZ());
        return length > 0 && eyeY > minY - length ? minY + 0.01 : eyeY;
    }
}

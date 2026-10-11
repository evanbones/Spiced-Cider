package com.evandev.spicedcider.mixin.voidwater;

import com.evandev.spicedcider.voidwater.ServerVoidTrails;
import com.evandev.spicedcider.voidwater.VoidTrailColumns;
import com.evandev.spicedcider.voidwater.VoidTrailLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Level.class)
public abstract class LevelMixin implements VoidTrailLevel {
    @Unique
    @Nullable
    private VoidTrailColumns spicedcider$voidTrails;

    @Unique
    @Nullable
    private ServerVoidTrails spicedcider$serverVoidTrails;

    @Override
    public @Nullable VoidTrailColumns spicedcider$voidTrails() {
        return spicedcider$voidTrails;
    }

    @Override
    public @Nullable ServerVoidTrails spicedcider$serverVoidTrails() {
        return spicedcider$serverVoidTrails;
    }

    @Override
    public void spicedcider$setVoidTrails(VoidTrailColumns columns, @Nullable ServerVoidTrails server) {
        spicedcider$voidTrails = columns;
        spicedcider$serverVoidTrails = server;
    }

    @ModifyVariable(method = "getFluidState", at = @At("HEAD"), argsOnly = true)
    private BlockPos spicedcider$extendFluidIntoVoid(BlockPos pos) {
        VoidTrailColumns columns = spicedcider$voidTrails;
        if (columns == null) return pos;
        int minY = ((Level) (Object) this).getMinBuildHeight();
        if (pos.getY() >= minY) return pos;
        int length = columns.get(pos.getX(), pos.getZ());
        return length > 0 && pos.getY() >= minY - length ? pos.atY(minY) : pos;
    }
}

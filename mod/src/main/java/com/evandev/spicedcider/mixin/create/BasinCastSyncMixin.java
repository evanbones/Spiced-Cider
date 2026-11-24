package com.evandev.spicedcider.mixin.create;

import com.evandev.spicedcider.compat.create.BasinCastSync;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("create")
@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinCastSyncMixin {
    @Inject(method = "write", at = @At("TAIL"))
    private void spicedcider$writeCast(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (clientPacket) BasinCastSync.write((BasinBlockEntity) (Object) this, tag, registries);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void spicedcider$readCast(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (clientPacket) BasinCastSync.read((BasinBlockEntity) (Object) this, tag, registries);
    }
}

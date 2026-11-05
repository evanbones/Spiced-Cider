package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.content.entity.PaleSnowGolem;
import com.evandev.spicedcider.registry.ModPaleGardenBlocks;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowGolem.class)
public abstract class SnowGolemPalePumpkinMixin implements PaleSnowGolem {
    @Unique
    private static final byte SPICEDCIDER$PALE_FLAG = 32;
    @Unique
    private static final String SPICEDCIDER$PALE_KEY = "spicedcider:PalePumpkin";

    @Shadow
    @Final
    private static EntityDataAccessor<Byte> DATA_PUMPKIN_ID;

    @Unique
    private boolean spicedcider$paleFlag() {
        return (((SnowGolem) (Object) this).getEntityData().get(DATA_PUMPKIN_ID) & SPICEDCIDER$PALE_FLAG) != 0;
    }

    @Override
    public boolean spicedcider$hasPalePumpkin() {
        return ((SnowGolem) (Object) this).hasPumpkin() && this.spicedcider$paleFlag();
    }

    @Override
    public void spicedcider$setPalePumpkin(boolean pale) {
        SnowGolem self = (SnowGolem) (Object) this;
        byte flags = self.getEntityData().get(DATA_PUMPKIN_ID);
        self.getEntityData().set(DATA_PUMPKIN_ID, (byte) (pale ? flags | SPICEDCIDER$PALE_FLAG : flags & ~SPICEDCIDER$PALE_FLAG));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void spicedcider$savePale(CompoundTag tag, CallbackInfo ci) {
        if (this.spicedcider$hasPalePumpkin()) tag.putBoolean(SPICEDCIDER$PALE_KEY, true);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void spicedcider$loadPale(CompoundTag tag, CallbackInfo ci) {
        this.spicedcider$setPalePumpkin(tag.getBoolean(SPICEDCIDER$PALE_KEY));
    }

    @ModifyExpressionValue(method = "shear", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;CARVED_PUMPKIN:Lnet/minecraft/world/item/Item;"))
    private Item spicedcider$dropPale(Item original) {
        if (!this.spicedcider$paleFlag()) return original;
        this.spicedcider$setPalePumpkin(false);
        return ModPaleGardenBlocks.CARVED_PALE_PUMPKIN_ITEM.get();
    }
}

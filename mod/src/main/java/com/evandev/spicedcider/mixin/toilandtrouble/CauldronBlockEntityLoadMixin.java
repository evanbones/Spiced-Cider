package com.evandev.spicedcider.mixin.toilandtrouble;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("toil_and_trouble")
@Mixin(targets = "cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity", remap = false)
public class CauldronBlockEntityLoadMixin {

    @Inject(method = "loadAdditional", at = @At("HEAD"))
    private void spicedcider$fillMissingKeys(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
        if (!tag.contains("cauldron.inventory", Tag.TAG_LIST)) {
            tag.put("cauldron.inventory", new ListTag());
        }
        if (!tag.contains("cauldron.potion")) {
            CompoundTag empty = new CompoundTag();
            empty.putString("id", "minecraft:air");
            empty.putInt("amount", 0);
            tag.put("cauldron.potion", empty);
        }
        if (!tag.contains("cauldron.max_progress", Tag.TAG_INT)) {
            tag.putInt("cauldron.max_progress", 60);
        }
    }
}

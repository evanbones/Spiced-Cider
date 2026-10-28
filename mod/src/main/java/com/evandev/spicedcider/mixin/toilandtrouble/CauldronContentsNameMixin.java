package com.evandev.spicedcider.mixin.toilandtrouble;

import cc.cassian.cauldrons.blocks.entity.CauldronBlockEntity;
import com.evandev.spicedcider.content.brewing.CauldronBrewing;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModLoaded("toil_and_trouble")
@Mixin(value = CauldronBlockEntity.class, remap = false)
public class CauldronContentsNameMixin {

    @Inject(method = "getContentsName", at = @At("HEAD"), cancellable = true)
    private void spicedcider$translatePotionName(CallbackInfoReturnable<Component> cir) {
        CauldronBlockEntity cauldron = (CauldronBlockEntity) (Object) this;
        if (cauldron.getContents().customName().isEmpty() && cauldron.getContents().customEffects().isEmpty()
                && CauldronBrewing.isWater(cauldron.getContents().potion())) {
            cir.setReturnValue(Blocks.WATER_CAULDRON.getName());
        } else if (cauldron.getContents().customName().isEmpty() && cauldron.getContents().isPotion()) {
            cir.setReturnValue(Component.translatable(Potion.getName(cauldron.getContents().potion(), "item.minecraft.potion.effect.")));
        }
    }
}

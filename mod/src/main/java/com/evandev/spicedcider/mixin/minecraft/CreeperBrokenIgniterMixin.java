package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.util.BrokenItemUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Creeper.class)
public class CreeperBrokenIgniterMixin {

    @WrapOperation(
            method = "mobInteract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0)
    )
    private boolean cider$brokenIgniterCantIgnite(ItemStack stack, TagKey<Item> tag, Operation<Boolean> original) {
        return !BrokenItemUtil.isBroken(stack) && original.call(stack, tag);
    }
}

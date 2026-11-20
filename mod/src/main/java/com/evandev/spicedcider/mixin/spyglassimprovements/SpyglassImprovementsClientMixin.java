package com.evandev.spicedcider.mixin.spyglassimprovements;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("spyglass_improvements")
@Mixin(targets = "me.juancarloscp52.spyglass_improvements.client.SpyglassImprovementsClient", remap = false)
public class SpyglassImprovementsClientMixin {

    @Shadow
    public static int slot;

    @ModifyExpressionValue(
            method = "onClientTick(Lnet/minecraft/client/Minecraft;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isCreative()Z")
    )
    private boolean spicedcider$zoomWithCarriedSpyglass(boolean creative, @Local LocalPlayer player) {
        return creative || slot < 0 && spicedcider$carriesSpyglass(player);
    }

    private static boolean spicedcider$carriesSpyglass(LocalPlayer player) {
        for (ItemStack stack : player.getInventory().items) {
            if (spicedcider$inBundle(stack, 0)) return true;
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (spicedcider$inBundle(stack, 0)) return true;
        }
        ItemContainerContents backpack = player.getItemBySlot(EquipmentSlot.CHEST).get(DataComponents.CONTAINER);
        if (backpack == null) return false;
        for (ItemStack inner : backpack.nonEmptyItems()) {
            if (inner.is(Items.SPYGLASS) || spicedcider$inBundle(inner, 0)) return true;
        }
        return false;
    }

    private static boolean spicedcider$inBundle(ItemStack stack, int depth) {
        BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
        if (bundle == null || depth > 2) return false;
        for (ItemStack inner : bundle.items()) {
            if (inner.is(Items.SPYGLASS) || spicedcider$inBundle(inner, depth + 1)) return true;
        }
        return false;
    }
}

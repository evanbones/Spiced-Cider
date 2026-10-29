package com.evandev.spicedcider.mixin.clutternomore;

import com.evandev.spicedcider.SpicedCider;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import dev.tazer.clutternomore.network.ChangeShape;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("clutternomore")
@Mixin(value = ChangeShape.class, remap = false)
public class ChangeShapeAdvancementMixin {

    @Unique
    private static final ResourceLocation spicedcider$ADVANCEMENT = ResourceLocation.fromNamespaceAndPath(SpicedCider.MOD_ID, "spiced_cider/getting_in_shape");

    @WrapOperation(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setItemInHand(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void spicedcider$awardHeld(ServerPlayer player, InteractionHand hand, ItemStack stack, Operation<Void> original) {
        original.call(player, hand, stack);
        spicedcider$award(player);
    }

    @WrapOperation(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;setByPlayer(Lnet/minecraft/world/item/ItemStack;)V"))
    private static void spicedcider$awardSlot(Slot slot, ItemStack stack, Operation<Void> original, @Local(argsOnly = true) ServerPlayer player) {
        original.call(slot, stack);
        spicedcider$award(player);
    }

    @Unique
    private static void spicedcider$award(ServerPlayer player) {
        AdvancementHolder holder = player.server.getAdvancements().get(spicedcider$ADVANCEMENT);
        if (holder != null) player.getAdvancements().award(holder, "changed_shape");
    }
}

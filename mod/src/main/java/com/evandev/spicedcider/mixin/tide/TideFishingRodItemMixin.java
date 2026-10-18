package com.evandev.spicedcider.mixin.tide;

import com.evandev.spicedcider.content.handler.GrapplingHookHandler;
import com.evandev.spicedcider.registry.ModItems;
import com.li64.tide.data.rods.CustomRodManager;
import com.li64.tide.registries.items.TideFishingRodItem;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@IfModLoaded("tide")
@Mixin(value = TideFishingRodItem.class, remap = false)
public class TideFishingRodItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack rod = player.getItemInHand(hand);
        ItemStack hookStack = CustomRodManager.getHook(rod);
        if (hookStack.is(ModItems.GRAPPLING_HOOK.get()) || hookStack.is(ModItems.STICKY_GRAPPLING_HOOK.get())) {
            cir.setReturnValue(GrapplingHookHandler.useGrapplingHook(level, player, hand, rod, hookStack));
        }
    }

    @ModifyArg(method = "getDescriptionLines", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;"))
    private static Collection<Component> spicedcider$tearsRodTooltip(Collection<Component> lines, @Local(argsOnly = true) ItemStack rod) {
        if (ModItems.ADAMANTITE_FISHING_ROD != null && rod.is(ModItems.ADAMANTITE_FISHING_ROD.get())) {
            lines.add(Component.translatable("text.spicedcider.rod_tooltip.tears_bonus").withStyle(ChatFormatting.GOLD));
        }
        return lines;
    }
}

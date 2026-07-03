package com.evandev.spicedcider.mixin.minecraft;

import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackKeeperMixin {

    @Unique
    private boolean cider$isBroken() {
        if (!SpicedCiderConfig.COMMON.keepBrokenItems.get()) return false;

        ItemStack self = (ItemStack) (Object) this;
        int maxDamage = self.getMaxDamage() - (self.getItem() instanceof ElytraItem ? 1 : 0);
        return self.isDamageableItem() && self.getDamageValue() > 0 && self.getDamageValue() >= maxDamage;
    }

    @Unique
    private boolean cider$isKeeper() {
        ItemStack self = (ItemStack) (Object) this;
        return self.has(DataComponents.CUSTOM_NAME) || self.isEnchanted() || self.getItem() instanceof ElytraItem;
    }

    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    private void cider$brokenNoMiningSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        if (cider$isBroken()) {
            cir.setReturnValue(1.0F);
            cir.cancel();
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void cider$brokenDontUse(Level level, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (cider$isBroken()) {
            cir.setReturnValue(InteractionResultHolder.fail((ItemStack) (Object) this));
            cir.cancel();
        }
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void cider$brokenDontUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (cider$isBroken()) {
            cir.setReturnValue(InteractionResult.FAIL);
            cir.cancel();
        }
    }

    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    private void cider$brokenDontInteract(Player user, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (cider$isBroken()) {
            cir.setReturnValue(InteractionResult.FAIL);
            cir.cancel();
        }
    }

    @Inject(method = "isCorrectToolForDrops", at = @At("HEAD"), cancellable = true)
    private void cider$brokenNotCorrectTool(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (cider$isBroken()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", at = @At("HEAD"), cancellable = true)
    private void cider$brokenHasNoAttributes(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> consumer, CallbackInfo ci) {
        if (cider$isBroken()) {
            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamageableItem()Z")
    )
    private boolean cider$brokenNoDamage(boolean original) {
        return original && !(cider$isKeeper() && cider$isBroken());
    }

    @ModifyExpressionValue(
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxDamage()I")
    )
    private int cider$dontBreakKeepers(int original, int amount, ServerLevel level, @Nullable LivingEntity entity, Consumer<Item> onBreak) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.getDamageValue() >= original && cider$isKeeper()) {
            onBreak.accept(self.getItem());
            return Integer.MAX_VALUE;
        }
        return original;
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"), cancellable = true)
    public void cider$brokenShowTooltip(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        if (cider$isBroken()) {
            List<Component> list = new ArrayList<>(cir.getReturnValue());
            list.add(Component.translatable("item.spicedcider.broken").withStyle(ChatFormatting.DARK_RED));
            cir.setReturnValue(list);
        }
    }
}

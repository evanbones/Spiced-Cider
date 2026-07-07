package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.entities.projectiles.GrapplingHookEntity;
import com.evandev.spicedcider.interfaces.PlayerWithGrapplingHook;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class GrapplingHookHandler {

    public static InteractionResultHolder<ItemStack> useGrapplingHook(Level level, Player player, InteractionHand hand, ItemStack rod, ItemStack hookStack) {
        GrapplingHookEntity grapplingHook = ((PlayerWithGrapplingHook) player).spicedcider$getHook();

        if (grapplingHook != null) {
            if (!level.isClientSide()) {
                int rodDamage = grapplingHook.retrieve(rod);
                int currentDamage = rod.getMaxDamage() - rod.getDamageValue();
                if (rodDamage >= currentDamage) {
                    rodDamage = currentDamage;
                }
                rod.hurtAndBreak(rodDamage, player, Player.getSlotForHand(hand));
            }

            player.swing(hand);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);

            return InteractionResultHolder.sidedSuccess(rod, level.isClientSide());
        }

        if (!level.isClientSide()) {
            boolean sticky = hookStack.is(ModItems.STICKY_GRAPPLING_HOOK.get());
            GrapplingHookEntity bobber = new GrapplingHookEntity(player, level, hookStack, sticky);
            level.addFreshEntity(bobber);
        }

        player.swing(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
        player.gameEvent(GameEvent.ITEM_INTERACT_START);

        return InteractionResultHolder.sidedSuccess(rod, level.isClientSide());
    }
}

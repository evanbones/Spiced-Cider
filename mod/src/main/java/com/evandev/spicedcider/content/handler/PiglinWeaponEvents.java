package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class PiglinWeaponEvents {

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.loadedFromDisk() || event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof AbstractPiglin) && !(event.getEntity() instanceof ZombifiedPiglin)) return;
        Mob mob = (Mob) event.getEntity();
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
            ItemStack stack = mob.getItemBySlot(slot);
            Item replacement = stack.is(Items.GOLDEN_SWORD) ? ModItems.PIGLIN_BROADSWORD.get()
                    : stack.is(Items.GOLDEN_AXE) ? ModItems.PIGLIN_BATTLEAXE.get() : null;
            if (replacement != null) {
                mob.setItemSlot(slot, stack.transmuteCopy(replacement));
            }
        }
    }
}

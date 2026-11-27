package com.evandev.spicedcider.content.handler;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class SkeletonCrudeBowEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk()) return;
        if (!(event.getEntity() instanceof AbstractSkeleton skeleton)) return;
        if (!SpicedCiderConfig.commonOr(SpicedCiderConfig.COMMON.skeletonCrudeBows, false)) return;
        ItemStack held = skeleton.getMainHandItem();
        if (held.is(Items.BOW)) {
            skeleton.setItemSlot(EquipmentSlot.MAINHAND, held.transmuteCopy(ModItems.CRUDE_BOW.get(), held.getCount()));
        }
    }
}

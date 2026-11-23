package com.evandev.spicedcider.content.entity.skeleton;

import com.evandev.spicedcider.SpicedCider;
import com.evandev.spicedcider.config.SpicedCiderConfig;
import com.evandev.spicedcider.registry.ModAttachments;
import com.evandev.spicedcider.registry.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = SpicedCider.MOD_ID)
public class RangedSkeletons {
    private static final ResourceLocation KNIFE_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "flint_knife");
    private static final double DRAW_KNIFE_DISTANCE_SQR = 3.0 * 3.0;
    private static final double DRAW_BOW_DISTANCE_SQR = 5.0 * 5.0;
    private static final int CHECK_INTERVAL = 5;

    public static boolean applies(Mob mob) {
        return mob instanceof AbstractSkeleton skeleton && enabled(skeleton)
                && mob.isHolding(stack -> stack.getItem() instanceof BowItem);
    }

    private static boolean enabled(AbstractSkeleton skeleton) {
        return SpicedCiderConfig.COMMON.rangedSkeletons.get() && skeleton.getType().is(ModTags.EntityTypes.RANGED_SKELETONS);
    }

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof AbstractSkeleton skeleton) || skeleton.level().isClientSide()
                || skeleton.tickCount % CHECK_INTERVAL != 0) return;
        boolean knifeOut = skeleton.hasData(ModAttachments.SHEATHED_BOW);
        if (!enabled(skeleton)) {
            if (knifeOut) drawBow(skeleton);
            return;
        }
        LivingEntity target = skeleton.getTarget();
        double distance = target != null && target.isAlive() ? skeleton.distanceToSqr(target) : Double.MAX_VALUE;
        if (knifeOut) {
            if (distance >= DRAW_BOW_DISTANCE_SQR) drawBow(skeleton);
        } else if (distance <= DRAW_KNIFE_DISTANCE_SQR) {
            drawKnife(skeleton);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof AbstractSkeleton skeleton && skeleton.hasData(ModAttachments.SHEATHED_BOW)) {
            drawBow(skeleton);
        }
    }

    private static void drawKnife(AbstractSkeleton skeleton) {
        ItemStack bow = skeleton.getMainHandItem();
        Item knife = BuiltInRegistries.ITEM.getOptional(KNIFE_ID).orElse(null);
        if (knife == null || !(bow.getItem() instanceof BowItem)) return;
        skeleton.stopUsingItem();
        skeleton.setData(ModAttachments.SHEATHED_BOW, bow);
        skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(knife));
    }

    private static void drawBow(AbstractSkeleton skeleton) {
        ItemStack bow = skeleton.removeData(ModAttachments.SHEATHED_BOW);
        Item knife = BuiltInRegistries.ITEM.getOptional(KNIFE_ID).orElse(null);
        if (bow == null || knife == null || !skeleton.getMainHandItem().is(knife)) return;
        skeleton.setItemSlot(EquipmentSlot.MAINHAND, bow);
    }
}
